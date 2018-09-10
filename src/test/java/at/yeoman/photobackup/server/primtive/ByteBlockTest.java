package at.yeoman.photobackup.server.primtive;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ByteBlockTest {
    @Test
    public void toStringIsHex() {
        byte[] data = new byte[] { 1, 10, -128, -1 };
        ByteBlock byteBlock = new ByteBlock(data, data.length);
        assertEquals("010a80ff", byteBlock.toString());
    }
}
