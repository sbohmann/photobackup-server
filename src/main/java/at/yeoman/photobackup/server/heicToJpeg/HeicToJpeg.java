package at.yeoman.photobackup.server.heicToJpeg;

import java.io.File;

public class HeicToJpeg {
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

    native static byte[] convert(byte[] heicData);
}
