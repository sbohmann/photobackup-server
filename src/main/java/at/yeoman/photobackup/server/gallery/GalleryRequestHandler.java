package at.yeoman.photobackup.server.gallery;

import at.yeoman.photobackup.server.api.ResourceDescription;
import at.yeoman.photobackup.server.core.Core;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.List;

@Controller
public class GalleryRequestHandler {
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
    public List<ResourceDescription> imageList(Model model, @PathVariable(required = false) String date) {
        if (date != null) {
            LocalDate parsedDate = LocalDate.parse(date);
            return new ResourcesForDate(core, parsedDate).result;
        } else {
            return new ResourcesForDate(core, null).result;
        }
    }
}
