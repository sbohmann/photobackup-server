package at.yeoman.photobackup.server.service;

import at.yeoman.photobackup.server.Directories;
import at.yeoman.photobackup.server.api.AssetReport;
import at.yeoman.photobackup.server.api.MissingAssets;
import at.yeoman.photobackup.server.assets.Checksum;
import at.yeoman.photobackup.server.assets.ResourceDescription;
import at.yeoman.photobackup.server.core.Core;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.channels.FileLock;
import java.security.MessageDigest;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static at.yeoman.photobackup.server.io.TransactionalFileHandling.finishAndSync;

@RestController
public class BackupRequestHandler {
    // TODO if true, at least don't double re-calculate the same resource for > 1 assets
    private static final boolean CalculateChecksumOfExistingResource = false;

    private final Logger log = LoggerFactory.getLogger(BackupRequestHandler.class);

    private final Core core;

    private final Set<Checksum> checksumsUploading = ConcurrentHashMap.newKeySet();

    @Autowired
    BackupRequestHandler(Core core) throws IOException {
        this.core = core;
    }

    @GetMapping("/")
    public String root() {
        return "photobackup server";
    }

    @PostMapping("/asset-report")
    public MissingAssets handleAssetReport(@RequestBody AssetReport report) {
        log.info("Received asset report with " + report.getDescriptions().size() + " assets.");
        core.reportAssets(report);
        MissingAssets result = new MissingAssets();
        List<Checksum> checksums = report
                .getDescriptions()
                .stream()
                .flatMap(x -> x.getResourceDescriptions().stream())
                .map(ResourceDescription::getChecksum)
                .filter(this::missing)
                .distinct()
                .collect(Collectors.toList());
        result.setMissingAssetChecksums(checksums);
        return result;
    }

    private boolean missing(Checksum checksum) {
        try {
            return !backupExists(checksum);
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            return true;
        }
    }

    private boolean backupExists(Checksum checksum) throws Exception {
        File file = new File(Directories.Photos, fileNameForChecksum(checksum));
        log.info("File [" + file + "] exists for checksum [" + checksum + "]: " + file.exists() + ", is file: " + file.isFile());
        if (!file.isFile()) {
            return false;
        }
        if (CalculateChecksumOfExistingResource) {
            Checksum calculatedChecksum = checksumForFile(file);
            log.info("Calculated checksum [" + checksum + "] matches: " + calculatedChecksum.equals(checksum));
            return calculatedChecksum.equals(checksum);
        } else {
            return true;
        }
    }

    @PostMapping("/resource-upload/{checksumString}")
    public ResponseEntity<String> handleResourceUpload(@PathVariable final String checksumString, InputStream bodyStream) throws Exception {
        try {
            final Checksum checksumFromPath;
            try {
                checksumFromPath = new Checksum(checksumString);
            } catch (Exception exception) {
                log.error(exception.getMessage(), exception);
                return error("Illegal checksum string from path [" + checksumString + "]",
                        HttpStatus.BAD_REQUEST);
            }
            if (!checksumsUploading.add(checksumFromPath)) {
                return error("Attempt to concurrently upload file of same checksum " + checksumFromPath,
                        HttpStatus.EXPECTATION_FAILED);
            }
            try {
                String fileName = fileNameForChecksum(checksumFromPath);
                File uploadTarget = new File(Directories.Upload, fileName);
                final Checksum calculatedChecksum = writeFile(bodyStream, uploadTarget);
                if (!calculatedChecksum.equals(checksumFromPath)) {
                    return error("Checksum mismatch - checksum from request path: " + checksumFromPath +
                                    ", calculated: " + calculatedChecksum,
                            HttpStatus.EXPECTATION_FAILED);
                }
                File renamedTarget = new File(Directories.Photos, fileName);
                if (renamedTarget.exists()) {
                    if (!renamedTarget.isFile()) {
                        return error("renamed target [" + renamedTarget.getCanonicalPath() + "] is not a file",
                                HttpStatus.EXPECTATION_FAILED);
                    } else if (renamedTarget.length() > uploadTarget.length()) {
                        return error("renamed target [" + renamedTarget.getCanonicalPath() + "] length [" + renamedTarget.length() + "]" +
                                        " > upload target [" + uploadTarget.getCanonicalPath() + "] length [" + uploadTarget.length() + "]",
                                HttpStatus.EXPECTATION_FAILED);
                    } else if (renamedTarget.length() == uploadTarget.length() && checksumForFile(uploadTarget).equals(checksumFromPath)) {
                        if (!uploadTarget.delete()) {
                            log.error("Unable to delete upload target while retaining existing backup [" + uploadTarget.getCanonicalPath() + "]");
                        }
                        return success("Retaining matching file [" + renamedTarget.getCanonicalPath());
                    } else {
                        if (!renamedTarget.delete()) {
                            return error("Unable to delete non-matching renamed target [" + renamedTarget.getCanonicalPath() + "]",
                                    HttpStatus.INTERNAL_SERVER_ERROR);
                        }
                    }
                }
                if (!uploadTarget.renameTo(renamedTarget)) {
                    return error("Unable to rename [" + uploadTarget.getCanonicalPath() + "] to [" + renamedTarget.getCanonicalPath() + "]",
                            HttpStatus.INTERNAL_SERVER_ERROR);
                }
                return success(calculatedChecksum.toString());
            } finally {
                checksumsUploading.remove(checksumFromPath);
            }
        } catch (Exception exception) {
            bodyStream.close();
            log.error(exception.getMessage(), exception);
            return error("Error message: [" + exception.getMessage() + "]",
                    HttpStatus.EXPECTATION_FAILED);
        }
    }

    private String fileNameForChecksum(Checksum checksum) {
        return checksum.getValue().toRawString() + ".jpg";
    }

    private ResponseEntity<String> success(String message) {
        log.info(message);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    private ResponseEntity<String> error(String message, HttpStatus status) {
        log.error(message);
        return new ResponseEntity<>(message, status);
    }

    @SuppressWarnings("unused")
    private Checksum writeFile(InputStream in, File target) throws Exception {
        log.info("writing to file [" + target.getCanonicalPath() + "]...");
        try (FileOutputStream out = new FileOutputStream(target)) {
            out.getChannel().lock();
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] buffer = new byte[1024 * 1024];
            long bytesWritten = 0;
            while (true) {
                int n = in.read(buffer);
                if (n < 0) {
                    break;
                }
                out.write(buffer, 0, n);
                md.update(buffer, 0, n);
                bytesWritten += n;
            }
            finishAndSync(out, log, target);
            log.info("finished writing " + bytesWritten + " bytes to file [" + target.getCanonicalPath() + "].");
            return new Checksum(md.digest());
        }
    }

    private Checksum checksumForFile(File file) throws Exception {
        try (FileInputStream in = new FileInputStream(file)) {
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
}
