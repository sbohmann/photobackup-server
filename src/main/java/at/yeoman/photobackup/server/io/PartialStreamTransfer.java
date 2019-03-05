package at.yeoman.photobackup.server.io;

import org.apache.catalina.connector.ClientAbortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PartialStreamTransfer {
    private static final Logger log = LoggerFactory.getLogger(PartialStreamTransfer.class);

    public static long copy(InputStream in, OutputStream out, long offset, long length) throws IOException {
        byte[] buffer = new byte[4096];

        long skipped = 0;
        while (skipped < offset) {
            in.skip(offset - skipped);
        }

        long written = 0;
        try {
            while (written < length) {
                int maxmimum = (int) Math.min(buffer.length, length - written);
                int n = in.read(buffer, 0, maxmimum);
                if (n < 0) {
                    break;
                }
                if (n == 0) {
                    throw new IOException("read returned 0");
                }
                out.write(buffer, 0, n);
                written += n;
            }
        } catch(ClientAbortException error) {
            log.info("Client closed connection after writing " + written + " out of " + length + " bytes");
            throw error;
        }
        return written;
    }
}
