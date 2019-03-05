package at.yeoman.photobackup.server.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PartialStreamTransfer {
    public static long copy(InputStream in, OutputStream out, long offset, long length) throws IOException {
        byte[] buffer = new byte[4096];

        long skipped = 0;
        while (skipped < offset) {
            in.skip(offset - skipped);
        }

        long written = 0;
        while (written < length) {
            int maxmimum = (int) Math.min(buffer.length, length - written);
            int n = in.read(buffer, 0, maxmimum);
            if (n < 0) {
                break;
            }
            if (n == 0) {
                throw new IOException("read returned 0");
            }
            out.write(buffer);
            written += n;
        }
        return written;
    }
}
