package at.yeoman.photobackup.server.gallery;

import at.yeoman.photobackup.server.Directories;
import at.yeoman.photobackup.server.assets.AssetDescription;
import at.yeoman.photobackup.server.assets.Checksum;
import at.yeoman.photobackup.server.core.Core;
import at.yeoman.photobackup.server.imageMagick.ImageMagick;
import at.yeoman.photobackup.server.io.StreamTransfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Controller
public class GalleryRequestHandler {
    private static final Logger log = LoggerFactory.getLogger(GalleryRequestHandler.class);

    private final Core core;
    private final Thumbnails thumbnails;

    @Autowired
    public GalleryRequestHandler(Core core, Thumbnails thumbnails) {
        this.core = core;
        this.thumbnails = thumbnails;
    }

    @GetMapping(value = {"/gallery", "/gallery/{date}"})
    public String gallery(Model model, @PathVariable(required = false) String date) {
        if (date != null) {
            model.addAttribute("date", LocalDate.parse(date));
        } else {
            model.addAttribute("date", "any");
        }
        return "gallery/gallery";
    }

    @GetMapping(value = {"/gallery/imageList", "/gallery/imageList/{date}"})
    @ResponseBody
    public List<AssetDescription> imageList(@PathVariable(required = false) String date) {
        if (date != null) {
            YearAndMonth yearAndMonth = new YearAndMonth(date);
            if (yearAndMonth.valid) {
                return new AssetsForMonth(core, yearAndMonth.year, yearAndMonth.month).result;
            } else {
                try {
                    LocalDate parsedDate = LocalDate.parse(date);
                    return new AssetsForDate(core, parsedDate).result;
                } catch(DateTimeException error) {
                    log.error("Invalid date argument [" + date + "]", error);
                    return Collections.emptyList();
                }
            }
        } else {
            return new AssetsForDate(core, null).result;
        }
    }

    @GetMapping(value = ("/photos/{checksum}/*"), produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public void resource(@PathVariable Checksum checksum,
                         //@PathVariable(required = false) String fileName,
                         HttpServletResponse response)
            throws IOException {
        File file = new File(Directories.Photos, checksum.toRawString());
        if (file.isFile()) {
            response.setHeader("Content-Length", Long.toString(file.length()));
            try (FileInputStream in = new FileInputStream(file);
                 ServletOutputStream out = response.getOutputStream()) {
                long written = StreamTransfer.copy(in, out);
                if (written != file.length()) {
                    log.error("File size: " + file.length() + ", written: " + written + " for " + checksum);
                }
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown resource [" + checksum.toRawString() + "]");
        }
    }

    @GetMapping(value = ("/photos/{checksum}/converted/*"), produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public void convertedResource(@PathVariable Checksum checksum,
                                  @PathVariable(required = false) String fileName,
                                  HttpServletResponse response)
            throws IOException {
        File file = new File(Directories.Photos, checksum.toRawString());
        if (file.isFile()) {
            try (FileInputStream in = new FileInputStream(file)) {
                ByteArrayOutputStream originalBuffer = createFileBuffer(file);
                long written = StreamTransfer.copy(in, originalBuffer);
                if (written != file.length()) {
                    log.error("File size: " + file.length() + ", written: " + written + " for " + checksum);
                }
                byte[] convertedBuffer = ImageMagick.convertToJpeg(originalBuffer.toByteArray());
                log.info("Original file size: " + file.length() + ", converted size: " + convertedBuffer.length +
                        " for " + fileName + ", " + checksum);
                response.setHeader("Content-Length", Long.toString(convertedBuffer.length));
                try (ServletOutputStream out = response.getOutputStream()) {
                    written = StreamTransfer.copy(new ByteArrayInputStream(convertedBuffer), out);
                    if (written != convertedBuffer.length) {
                        log.error("Converted buffer length: " + convertedBuffer.length +
                                ", written: " + written + " for " + checksum);
                    }
                }
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Unknown resource [" + checksum.toRawString() + "]");
        }
    }

    @GetMapping(value = ("/photos/{checksum}/thumbnail/*"), produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public void thumbnail(@PathVariable Checksum checksum,
                          HttpServletResponse response)
            throws IOException {
        byte[] thumbnailData = thumbnails.get(checksum);
        if (thumbnailData != null) {
            response.setHeader("Content-Length", Long.toString(thumbnailData.length));
            ByteArrayInputStream in = new ByteArrayInputStream(thumbnailData);
            long written = StreamTransfer.copy(in, response.getOutputStream());
            if (written != thumbnailData.length) {
                log.error("Thumbnail data length: " + thumbnailData.length +
                        ", written: " + written + " for " + checksum);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No thumbnail available for resource [" + checksum.toRawString() + "]");
        }
    }

    private ByteArrayOutputStream createFileBuffer(File file) {
        long size = file.length();
        if (size <= Integer.MAX_VALUE) {
            return new ByteArrayOutputStream((int) size);
        } else {
            throw new IllegalArgumentException("File size > Integer.MAX_VALUE: " + size);
        }
    }
}
