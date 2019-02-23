package at.yeoman.photobackup.server.imageMagick;

import at.yeoman.photobackup.server.io.StreamTransfer;
import org.junit.Test;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ImageMagickTest {
    @Test
    public void loadLibrary() {
        ImageMagick.convertToJpeg(new byte[0]);
    }

    @Test
    public void convertFileToJpeg() throws IOException, InterruptedException {
        byte[] heicData = readHeicFile();
        System.out.println(heicData.length);

        Consumer<Integer> convertImage = index -> {
            byte[] jpegData = ImageMagick.convertToJpeg(heicData);
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
        String fileName = "example_image.heic";
        InputStream in = getClass().getResourceAsStream(fileName);
        if (in == null) {
            throw new RuntimeException("Unable to read resource " + getClass().getPackage().getName() + "/" + fileName);
        }
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        StreamTransfer.copy(in, buffer);
        return buffer.toByteArray();
    }

    private void writeJpegFile(byte[] jpegData) {
        try {
            FileOutputStream out = new FileOutputStream("converted.jpg");
            out.write(jpegData);
        } catch (IOException error) {
            throw new RuntimeException(error);
        }
    }
}
