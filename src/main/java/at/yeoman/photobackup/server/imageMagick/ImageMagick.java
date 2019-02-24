package at.yeoman.photobackup.server.imageMagick;

public class ImageMagick {
    static {
        String osName = System.getProperty("os.name");
        System.out.println("os.name: [" + osName + "]");
        boolean windows = osName.startsWith("Windows");
        boolean linux = osName.startsWith("Linux");

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
        } else if (linux) {
            System.loadLibrary("MagickCore-7.Q16HDRI");
            System.loadLibrary("MagickWand-7.Q16HDRI");
        } else {
            System.loadLibrary("MagickCore-7");
            System.loadLibrary("MagickWand-7");
        }
        System.loadLibrary("photobackup_server_native");

        initialize();
    }

    native private static void initialize();

    native public static byte[] convertToJpeg(byte[] heicData);

    native public static byte[] convertToJpegWithMaximumSize(byte[] originalImageFileContent,
                                                             int maximumWidth, int maximumHeight);
}
