package at.yeoman.photobackup.server.heicToJpeg;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class HeicToJpegTest {
    @Test
    public void convertHeicFile() throws IOException {
        byte[] heicData = readHeicFile();
        System.out.println(heicData.length);
        byte[] jpegData = HeicToJpeg.convert(heicData);
        System.out.println(jpegData.length);
    }

    private byte[] readHeicFile() throws IOException {
        File heicFile = new File("e:/tmp/original.heic");
        FileInputStream in = new FileInputStream(heicFile);
        if (heicFile.length() > Integer.MAX_VALUE) {
            throw new IOException("File too big: " + heicFile.length() + " bytes");
        }
        byte[] heicData = new byte[(int) heicFile.length()];
        int bytesRead = 0;
        while (bytesRead != heicData.length) {
            int n = in.read(heicData, bytesRead, heicData.length - bytesRead);
            if (n < 0) {
                throw new IOException("Reached EOF after " + bytesRead + " out of " + heicData.length + " bytes");
            }
            bytesRead += n;
        }
        if (in.read() != -1) {
            throw new IOException("EOF not reached after reading " + bytesRead + " bytes");
        }
        return heicData;
    }
}
