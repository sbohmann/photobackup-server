package at.yeoman.photobackup.server.gallery;

import at.yeoman.photobackup.server.Directories;
import at.yeoman.photobackup.server.assets.AssetDescription;
import at.yeoman.photobackup.server.assets.Checksum;
import at.yeoman.photobackup.server.core.Core;
import at.yeoman.photobackup.server.imageMagick.ImageMagick;
import at.yeoman.photobackup.server.io.PartialStreamTransfer;
import at.yeoman.photobackup.server.io.StreamTransfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
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
            YearAndMonth yearAndMonth = new YearAndMonth(date);
            if (yearAndMonth.valid) {
                model.addAttribute("date",
                        String.format("%04d-%02d", yearAndMonth.year, yearAndMonth.month));
            } else {
                model.addAttribute("date", LocalDate.parse(date));
            }
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
                } catch (DateTimeParseException error) {
                    log.error("Invalid date argument [" + date + "]", error);
                    return Collections.emptyList();
                }
            }
        } else {
            return new AssetsForDate(core, null).result;
        }
    }

    @RequestMapping(value = ("/photos/{checksum}/*"),
            method = RequestMethod.HEAD,
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public void resourceHead(@PathVariable Checksum checksum,
                             //@PathVariable(required = false) String fileName,
                             HttpServletRequest request,
                             HttpServletResponse response) {
        File file = new File(Directories.Photos, checksum.toRawString());
        if (file.isFile()) {
            writeResourceResponseHeaders(request, response, file);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown resource [" + checksum.toRawString() + "]");
        }
    }

    @GetMapping(value = ("/photos/{checksum}/*"),
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public void resourceData(@PathVariable Checksum checksum,
                             //@PathVariable(required = false) String fileName,
                             HttpServletRequest request,
                             HttpServletResponse response)
            throws IOException {
        File file = new File(Directories.Photos, checksum.toRawString());
        if (file.isFile()) {
            Range range = writeResourceResponseHeaders(request, response, file);
            try (FileInputStream in = new FileInputStream(file);
                 ServletOutputStream out = response.getOutputStream()) {
                if (range != null) {
                    writePartialData(checksum, file.length(), in, out, range);
                } else {
                    writeFullData(checksum, file.length(), in, out);
                }
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown resource [" + checksum.toRawString() + "]");
        }
    }

    private Range writeResourceResponseHeaders(HttpServletRequest request, HttpServletResponse response, File file) {
        Range range = Range.parse(request.getHeader("Range"));
        if (range != null) {
            response.setStatus(HttpStatus.PARTIAL_CONTENT.value());
            long actualLength = partialFileLength(file.length(), range);
            response.setHeader("Content-Length", Long.toString(actualLength));
        } else {
            response.setHeader("Content-Length", Long.toString(file.length()));
        }
        response.setHeader("Accept-Ranges", "bytes");
        return range;
    }

    private void writePartialData(Checksum checksum, long fileLength, InputStream in, OutputStream out, Range range)
            throws IOException {
        long bytesToWrite = partialFileLength(fileLength, range);
        long written = PartialStreamTransfer.copy(in, out, range.first, bytesToWrite);
        if (written != bytesToWrite) {
            log.error("File size: " + fileLength + ", written: " + written + " for " + checksum);
        }
    }

    private void writeFullData(Checksum checksum, long fileLength, InputStream in, OutputStream out)
            throws IOException {
        long written = StreamTransfer.copy(in, out);
        if (written != fileLength) {
            log.error("File size: " + fileLength + ", written: " + written + " for " + checksum);
        }
    }

    private long partialFileLength(long fileLength, Range range) {
        long requestedContentLength = range.last - range.first + 1;
        long lengthToEndOfFile = fileLength - range.first;
        long result = Math.min(requestedContentLength, lengthToEndOfFile);
        result = Math.max(result, 0);
        return result;
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
