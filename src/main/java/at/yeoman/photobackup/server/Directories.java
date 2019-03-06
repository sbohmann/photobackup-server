package at.yeoman.photobackup.server;

import com.google.common.collect.ImmutableList;

import java.io.File;
import java.io.IOException;

import static com.google.common.collect.ImmutableList.Builder;

public class Directories {
    public static final File Assets;
    public static final File Upload;
    public static final File Photos;
    public static final File Thumbnails;
    public static final File Videos;

    static final ImmutableList<File> values;

    static {
        try {
            Builder<File> builder = ImmutableList.builder();
            Assets = getDirectory("assets", builder);
            Upload = getDirectory("upload", builder);
            Photos = getDirectory("photos", builder);
            Thumbnails = getDirectory("thumbnails", builder);
            Videos = getDirectory("videos", builder);
            values = builder.build();
        } catch (IOException error) {
            throw new RuntimeException(error);
        }
    }

    private static File getDirectory(String name, Builder<File> builder) throws IOException {
        File result = new File(name);
        if (!result.isDirectory()) {
            throw new IOException("Not a directory: " + result.getCanonicalPath());
        }
        builder.add(result);
        return result;
    }
}
