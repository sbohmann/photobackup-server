package at.yeoman.photobackup.server;

import java.io.File;
import java.io.IOException;

public class Directories {
    public static final File Assets;
    public static final File Upload;
    public static final File Photos;
    public static final File Thumbnails;
    public static final File Videos;

    static {
        try {
            Assets = getDirectory("assets");
            Upload = getDirectory("upload");
            Photos = getDirectory("photos");
            Thumbnails = getDirectory("thumbnails");
            Videos = getDirectory("videos");
        } catch (IOException error) {
            throw new RuntimeException(error);
        }
    }

    private static File getDirectory(String name) throws IOException {
        File result = new File(name);
        if (!result.isDirectory()) {
            throw new IOException("Not a directory: " + result.getCanonicalPath());
        }
        return result;
    }
}
