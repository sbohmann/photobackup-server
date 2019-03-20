package at.yeoman.photobackup.server.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamTransfer {
    public static long copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[4096];
        long written = 0;
        while (true) {
            int n = in.read(buffer);
            if (n < 0) {
                break;
            }
            if (n == 0) {
                throw new IOException("read returned 0");
            }
            out.write(buffer, 0, n);
            written += n;
        }
        return written;
    }
}
