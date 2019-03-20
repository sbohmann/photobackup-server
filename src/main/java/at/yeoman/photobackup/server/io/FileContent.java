package at.yeoman.photobackup.server.io;

import java.io.*;

public class FileContent {
    public static byte[] read(File file) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (InputStream in = new FileInputStream(file)) {
            StreamTransfer.copy(in, out);
        }
        return out.toByteArray();
    }
}
