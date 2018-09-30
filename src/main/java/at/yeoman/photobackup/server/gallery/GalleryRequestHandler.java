package at.yeoman.photobackup.server.gallery;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;

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
}
