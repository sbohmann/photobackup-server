package at.yeoman.photobackup.server.heicToJpeg;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class HeicToJpegTest {
    @Test
    public void convertHeicFile() throws IOException, InterruptedException {
        byte[] heicData = readHeicFile();
        System.out.println(heicData.length);

        Consumer<Integer> convertImage = index -> {
            byte[] jpegData = HeicToJpeg.convert(heicData);
            System.out.println(index + " - " + jpegData.length);
            if (index == 0) {
                writeJpegFile(jpegData);
            }
        };

        ExecutorService executor = Executors.newFixedThreadPool(5);
        for (int index = 0; index < 100; ++index) {
            int oida = index;
            executor.execute(() -> convertImage.accept(oida));
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        }
        System.out.println("Finished all threads");
    }

    private byte[] readHeicFile() throws IOException {
        File heicFile = new File("e:/tmp/photos/6AD4DE5BFDA456A85859C5D74DA2093CE33A430DB429AACA838FD996AE6F4A2B358E1CF98953FCCEA54C0130C4B524C00EFE9B974F390D47F66B8DF42B088369");
        //File heicFile = new File("e:/tmp/original.heic");
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

    private void writeJpegFile(byte[] jpegData) {
        try {
            FileOutputStream out = new FileOutputStream("e:/tmp/copy_from_java.jpg");
            out.write(jpegData);
        } catch (IOException error) {
            throw new RuntimeException(error);
        }
    }
}
