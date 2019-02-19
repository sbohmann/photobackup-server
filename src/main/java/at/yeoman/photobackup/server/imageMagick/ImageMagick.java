package at.yeoman.photobackup.server.imageMagick;

public class ImageMagick {
    static {
        boolean windows = System.getProperty("os.name").startsWith("Windows");

        if (windows) {
            System.loadLibrary("CORE_RL_zlib_");
            System.loadLibrary("CORE_RL_glib_");
            System.loadLibrary("CORE_RL_bzlib_");
            System.loadLibrary("CORE_RL_lcms_");
            System.loadLibrary("CORE_RL_libxml_");
            System.loadLibrary("CORE_RL_lqr_");
            System.loadLibrary("CORE_RL_ttf_");
            System.loadLibrary("CORE_RL_zlib_");
            System.loadLibrary("CORE_RL_MagickCore_");
            System.loadLibrary("CORE_RL_MagickWand_");
            System.loadLibrary("CORE_RL_Magick++_");
        } else {
            System.loadLibrary("z");
            System.loadLibrary("glib");
            System.loadLibrary("bzlib");
            System.loadLibrary("lcms");
            System.loadLibrary("libxml");
            System.loadLibrary("lqr");
            System.loadLibrary("ttf");
            System.loadLibrary("zlib");
            System.loadLibrary("MagickCore");
            System.loadLibrary("MagickWand");
        }
        System.loadLibrary("photobackup_server_native");

        initialize();
    }

    native private static void initialize();

    native public static byte[] convertToJpeg(byte[] heicData);

    native public static byte[] convertToJpegWithMaximumSize(byte[] originalImageFileContent,
                                                             int maximumWidth, int maximumHeight);
}
