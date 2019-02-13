package at.yeoman.photobackup.server.gallery;

import at.yeoman.photobackup.server.Directories;
import at.yeoman.photobackup.server.assets.AssetDescription;
import at.yeoman.photobackup.server.assets.Checksum;
import at.yeoman.photobackup.server.core.Core;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Controller
public class GalleryRequestHandler {
    private static final Logger log = LoggerFactory.getLogger(GalleryRequestHandler.class);

    private final Core core;

    @Autowired
    public GalleryRequestHandler(Core core) {
        this.core = core;
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
            LocalDate parsedDate = LocalDate.parse(date);
            return new AssetsForDate(core, parsedDate).result;
        } else {
            return new AssetsForDate(core, null).result;
        }
    }

    @GetMapping(value = ("/photos/{checksum}/*"), produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public void resource(@PathVariable Checksum checksum,
                         //@PathVariable(required = false) String filename,
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
}
