package at.yeoman.photobackup.server.gallery;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

@Controller
public class GalleryRequestHandler {
    @GetMapping("/gallery")
    public String greeting(Model model) {
        model.addAttribute("date", LocalDate.now());
        return "gallery";
    }
}
