package at.yeoman.photobackup.server.service;

import at.yeoman.photobackup.server.api.AssetReport;
import at.yeoman.photobackup.server.api.Checksum;
import at.yeoman.photobackup.server.api.MissingAssets;
import at.yeoman.photobackup.server.api.ResourceDescription;
import at.yeoman.photobackup.server.core.Core;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class RequestHandler {
    private Core core;

    @Autowired
    RequestHandler(Core core) {
        this.core = core;
    }

    @PostMapping("/asset-report")
    public @ResponseBody
    MissingAssets handleAssetReport(@RequestBody AssetReport report) {
        System.out.println(report);
        MissingAssets result = new MissingAssets();
        List<Checksum> checksums = report
                .getDescriptions()
                .stream()
                .flatMap(x -> x.getResourceDescriptions().stream())
                .map(ResourceDescription::getChecksum)
                .distinct()
                .collect(Collectors.toList());
        result.setMissingAssetChecksums(checksums);
        return result;
    }

    @PostMapping("/resource-upload/{checksumString}")
    public String handleResourceUpload(@PathVariable String checksumString, HttpServletRequest request) throws Exception {
        final Checksum checksumFromPath = new Checksum(checksumString);
        final Checksum calculatedChecksum = calculateChecksum(request.getInputStream());
        if (!calculatedChecksum.equals(checksumFromPath)) {
            throw new IOException("Checksum mismatch - checksum from request path: " + checksumFromPath +
                    ", calculated: " + calculatedChecksum);
        }
        return calculatedChecksum.toString();
    }

    private Checksum calculateChecksum(InputStream in) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        byte[] buffer = new byte[1024 * 1024];
        while (true) {
            int n = in.read(buffer);
            if (n < 0) {
                break;
            }
            md.update(buffer, 0, n);
        }
        return new Checksum(md.digest());
    }
}
