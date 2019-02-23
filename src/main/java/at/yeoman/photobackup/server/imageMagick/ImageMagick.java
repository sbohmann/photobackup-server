package at.yeoman.photobackup.server.imageMagick;

import java.io.File;
import java.io.FileFilter;
import java.util.Objects;

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
//            System.loadLibrary("z");
            //System.loadLibrary("glib");
//            System.loadLibrary("bz2");
//            System.loadLibrary("lcms");
//            System.loadLibrary("libxml");
//            System.loadLibrary("lqr");
//            System.loadLibrary("ttf");
//            System.loadLibrary("zlib");
            System.loadLibrary("MagickCore-7");
            System.loadLibrary("MagickWand-7");
            FileFilter isLibraryFile = file -> file.getName().endsWith(".so");
            File[] rawLibraryFiles = new File("libraries/coders")
                    .listFiles(isLibraryFile);
            File[] libraryFiles = Objects.requireNonNull(rawLibraryFiles);
            for (File libraryFile : libraryFiles) {
                System.load(libraryFile.getAbsolutePath());
            }
        }
        System.loadLibrary("photobackup_server_native");

        initialize();
    }

    native private static void initialize();

    native public static byte[] convertToJpeg(byte[] heicData);

    native public static byte[] convertToJpegWithMaximumSize(byte[] originalImageFileContent,
                                                             int maximumWidth, int maximumHeight);
}
