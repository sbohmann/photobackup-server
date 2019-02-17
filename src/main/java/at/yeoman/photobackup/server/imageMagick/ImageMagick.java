package at.yeoman.photobackup.server.imageMagick;

public class ImageMagick {
    static {
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
        System.loadLibrary("photobackup_server_native");

        initialize();
    }

    native private static void initialize();

    native public static byte[] convertToJpeg(byte[] heicData);

    native public static byte[] convertToJpegWithMaximumSize(byte[] originalImageFileContent,
                                                             int maximumWidth, int maximumHeight);
}