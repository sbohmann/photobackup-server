package at.yeoman.photobackup.server.service;

import at.yeoman.photobackup.server.api.AssetReport;
import at.yeoman.photobackup.server.api.MissingAssets;
import at.yeoman.photobackup.server.core.Core;
import at.yeoman.photobackup.server.api.Checksum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class RequestHandler {
    private Core core;

    @Autowired
    RequestHandler(Core core) {
        this.core = core;
    }

    @PostMapping("/image-upload/asset-report")
    public @ResponseBody
    MissingAssets handleAssetReport(@RequestBody AssetReport report) {
        System.out.println(report);
        MissingAssets result = new MissingAssets();
        result.setMissingAssetChecksums(List.of(new Checksum("adababababababababababababababababababababababababababababababababababababababababababababababababababababababababababababababab")));
        return result;
    }

//    @PostMapping("/image-upload/{checksumString}")
    @GetMapping("/image-upload/{checksumString}")
    public String index(@PathVariable String checksumString) {
        Checksum checksum = new Checksum(checksumString);
        return checksum.toString();
    }
}
