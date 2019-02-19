package at.yeoman.photobackup.server.imageMagick;

public class ImageMagick {
    static {
        boolean windows = System.getProperty("os.name").startsWith("Windows");
        String prefix = windows ? "CORE_RL_" : "";
        String suffix = windows ? "_" : "";
        System.loadLibrary(prefix + "zlib" + suffix);
        System.loadLibrary(prefix + "glib" + suffix);
        System.loadLibrary(prefix + "bzlib" + suffix);
        System.loadLibrary(prefix + "lcms" + suffix);
        System.loadLibrary(prefix + "libxml" + suffix);
        System.loadLibrary(prefix + "lqr" + suffix);
        System.loadLibrary(prefix + "ttf" + suffix);
        System.loadLibrary(prefix + "zlib" + suffix);
        System.loadLibrary(prefix + "MagickCore" + suffix);
        System.loadLibrary(prefix + "MagickWand" + suffix);
        System.loadLibrary("photobackup_server_native");

        initialize();
    }

    native private static void initialize();

    native public static byte[] convertToJpeg(byte[] heicData);

    native public static byte[] convertToJpegWithMaximumSize(byte[] originalImageFileContent,
                                                             int maximumWidth, int maximumHeight);
}
