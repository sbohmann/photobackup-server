package at.yeoman.photobackup.server.primtive;

import java.util.Arrays;

public final class ByteBlock {
    private final byte[] value;

    public ByteBlock(byte[] value, int expectedLength) {
        checkLength(value, expectedLength);
        this.value = value.clone();
    }

    public void checkLength(int expectedLength) {
        checkLength(value, expectedLength);
    }

    private void checkLength(byte[] value, int expectedLength) {
        if (value.length != expectedLength) {
            throw new IllegalArgumentException(
                    "length [" + value.length + "] != expected length [" + expectedLength + "]");
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(value.length * 2);
        for (int index = 0; index < value.length; ++index) {
            int byteValue = value[index];
            if (byteValue < 0) {
                byteValue += 256;
            }
            result.append(hexChar(byteValue >>> 4));
            result.append(hexChar(byteValue & 0x0f));
        }
        return result.toString();
    }

    private char hexChar(int nibble) {
        if (nibble >= 0 && nibble <= 9) {
            return (char) ('0' + nibble);
        } else if (nibble >= 0x0a && nibble <= 0x0f) {
            return (char) ('a' + (nibble - 0x0a));
        } else {
            throw new IllegalArgumentException("nibble value: " + nibble);
        }
    }
}
