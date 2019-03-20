package at.yeoman.photobackup.server.io;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static at.yeoman.photobackup.server.io.ByteArrayUtilities.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class StreamTransferTest {
    @Test
    public void emptyStream() throws IOException {
        copyIsEqual();
    }
    
    @Test
    public void smallStream() throws IOException {
        copyIsEqual(1, 2, 3);
    }
    
    @Test
    public void roundSize() throws IOException {
        copyIsEqual(randomData(4096 * 3));
        copyIsEqual(randomData(4096 * 7));
    }
    
    @Test
    public void superRoundSize() throws IOException {
        copyIsEqual(randomData(4096));
        copyIsEqual(randomData(4096 * 2));
        copyIsEqual(randomData(4096 * 4));
    }
    
    @Test
    public void belowRoundSize() throws IOException {
        copyIsEqual(randomData(4096 * 3 - 1));
        copyIsEqual(randomData(4096 * 7 - 2));
        copyIsEqual(randomData(4096 - 16));
        copyIsEqual(randomData(4096 * 2 - 12));
        copyIsEqual(randomData(4096 * 4 - 21));
    }
    
    @Test
    public void aboveRoundSize() throws IOException {
        copyIsEqual(randomData(4096 * 3 + 1));
        copyIsEqual(randomData(4096 * 7 + 2));
        copyIsEqual(randomData(4096 + 16));
        copyIsEqual(randomData(4096 * 2 + 12));
        copyIsEqual(randomData(4096 * 4 + 21));
    }
    
    @Test
    public void randomSize() throws IOException {
        for (int round = 1; round <= 100; ++round) {
            int size = random.nextInt(100) * random.nextInt(100) + random.nextInt(100);
            copyIsEqual(randomData(size));
        }
    }
    
    private void copyIsEqual(int... template) throws IOException {
        byte[] original = toByteArray(template);
        copyIsEqual(original);
    }
    
    private byte[] toByteArray(int[] template) {
        byte[] original = new byte[template.length];
        for (int index = 0; index < template.length; ++index) {
            original[index] = (byte) template[index];
        }
        return original;
    }
    
    private void copyIsEqual(byte[] original) throws IOException {
        InputStream in = new ByteArrayInputStream(original);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        long written = StreamTransfer.copy(in, out);
        byte[] copy = out.toByteArray();
        assertArrayEquals(original, copy);
        assertEquals(copy.length, written);
        report(original, copy);
    }
}
