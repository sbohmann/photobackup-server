package at.yeoman.photobackup.server.service;

import at.yeoman.photobackup.server.core.Core;
import at.yeoman.photobackup.server.api.Checksum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class RequestHandler {
    private Core core;

    @Autowired
    RequestHandler(Core core) {
        this.core = core;
    }

//    @PostMapping("/image-upload/{checksumString}")
    @GetMapping("/image-upload/{checksumString}")
    public String index(@PathVariable String checksumString) {
        Checksum checksum = new Checksum(checksumString);
        return checksum.toString();
    }
}