package at.yeoman.photobackup.server.gallery;

import at.yeoman.photobackup.server.assets.ImageType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Controller
public class GalleryRequestHandler {
    @GetMapping(value = { "/gallery", "/gallery/{date}" })
    public String gallery(Model model, @PathVariable(required = false) String date) {
        if (date != null) {
            model.addAttribute("date", LocalDate.parse(date));
        } else {
            model.addAttribute("date", "any");
        }
        return "gallery/gallery";
    }

    @GetMapping(value = { "/gallery/imageList", "/gallery/imageList/{date}" })
    @ResponseBody
    public List<ImageInfo> imageList(Model model, @PathVariable(required = false) String date) {
        if (date != null) {
            model.addAttribute("date", LocalDate.parse(date));
        } else {
            model.addAttribute("date", "any");
        }
        return Collections.singletonList(new ImageInfo(LocalDate.now(), ImageType.Video));
    }
}
