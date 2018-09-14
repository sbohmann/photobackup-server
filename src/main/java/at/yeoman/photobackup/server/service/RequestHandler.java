package at.yeoman.photobackup.server.service;

import at.yeoman.photobackup.server.api.AssetReport;
import at.yeoman.photobackup.server.api.Checksum;
import at.yeoman.photobackup.server.api.MissingAssets;
import at.yeoman.photobackup.server.api.ResourceDescription;
import at.yeoman.photobackup.server.core.Core;
import com.oracle.tools.packager.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class RequestHandler {
    private final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Core core;
    private File photoDirectory;

    @Autowired
    RequestHandler(Core core) throws IOException {
        this.core = core;
        photoDirectory = new File("photos");
        if (!photoDirectory.isDirectory()) {
            throw new IOException("Not a dirsctory: " + photoDirectory.getCanonicalPath());
        }
    }

    @GetMapping("/")
    public @ResponseBody
    String root() {
        return "photobackup server";
    }

    @PostMapping("/asset-report")
    public @ResponseBody
    MissingAssets handleAssetReport(@RequestBody AssetReport report) {
        log.info("Received asset report with " + report.getDescriptions().size() + " assets.");
        MissingAssets result = new MissingAssets();
        List<Checksum> checksums = report
                .getDescriptions()
                .stream()
                .flatMap(x -> x.getResourceDescriptions().stream())
                .map(ResourceDescription::getChecksum)
                .distinct()
                .collect(Collectors.toList());
        result.setMissingAssetChecksums(checksums);
        log.info("Responding with request for all resources - TODO fix this asap"); // TODO
        return result;
    }

    @PostMapping("/resource-upload/{checksumString}")
    @ResponseBody
    public ResponseEntity<String> handleResourceUpload(@PathVariable final String checksumString, InputStream bodyStream) throws Exception {
        final Checksum checksumFromPath;
        try {
            checksumFromPath = new Checksum(checksumString);
        } catch (Exception exception) {
            return new ResponseEntity<>("Illegal checksum string from path [" + checksumString + "]",
                    HttpStatus.BAD_REQUEST);
        }
        File target = new File(photoDirectory,  checksumString + ".jpg");
        final Checksum calculatedChecksum = writeFile(bodyStream, target);
        if (!calculatedChecksum.equals(checksumFromPath)) {
            return new ResponseEntity<>("Checksum mismatch - checksum from request path: " + checksumFromPath +
                    ", calculated: " + calculatedChecksum,
                    HttpStatus.EXPECTATION_FAILED);
        }
        return new ResponseEntity<>(calculatedChecksum.toString(), HttpStatus.OK);
    }

    private Checksum writeFile(InputStream in, File target) throws Exception {
        FileOutputStream out = new FileOutputStream(target);
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        byte[] buffer = new byte[1024 * 1024];
        while (true) {
            int n = in.read(buffer);
            if (n < 0) {
                break;
            }
            out.write(buffer, 0, n);
            md.update(buffer, 0, n);
        }
        out.getChannel().force(true);
        out.close();
        try {
            out.getFD().sync();
        } catch (SyncFailedException exception) {
            log.debug("Unable to sync file [" + target.getCanonicalPath() + "]", exception);
        }
        return new Checksum(md.digest());
    }
}
