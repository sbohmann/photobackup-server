package upload_exploration;

import at.yeoman.photobackup.server.assets.Checksum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.security.MessageDigest;

@RestController
public class UploadExplorationRequestHandler {
    private final Logger log = LoggerFactory.getLogger(UploadExplorationRequestHandler.class);

    @PostMapping("/upload_exploration")
    public ResponseEntity<String> handleResourceUpload(InputStream bodyStream) {
        try {
            final Checksum calculatedChecksum = processInputStream(bodyStream);
            return success(calculatedChecksum.toString());
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            return error("Error message: [" + exception.getMessage() + "]");
        } finally {
            try {
                bodyStream.close();
            } catch (Exception error) {
                log.error("Unable to close stream", error);
            }
        }
    }

    private Checksum processInputStream(InputStream in) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        byte[] buffer = new byte[1024 * 1024];
        long bytesProcessed = 0;
        while (true) {
            int n = in.read(buffer);
            if (n < 0) {
                break;
            }
            md.update(buffer, 0, n);
            bytesProcessed += n;
        }
        log.info("finished processing " + bytesProcessed + " bytes.");
        return new Checksum(md.digest());
    }

    private ResponseEntity<String> success(String message) {
        log.info(message);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    private ResponseEntity<String> error(String message) {
        log.error(message);
        return new ResponseEntity<>(message, HttpStatus.EXPECTATION_FAILED);
    }
}
