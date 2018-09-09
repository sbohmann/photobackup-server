package at.yeoman.photobackup.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {
    private Core core;

    @Autowired
    IndexController(Core core) {

        this.core = core;
    }

    @GetMapping("/index")
    public Qumquat index(
            @RequestParam(name="name", required=false, defaultValue="Default Value From Controller") String name) {
        return new Qumquat(3, core.createText(name));
    }
}
