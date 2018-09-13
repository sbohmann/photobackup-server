package at.yeoman.photobackup.server.api;

import at.yeoman.photobackup.server.primtive.ByteBlock;

public class Checksum {
    private static final int Length = 64;

    private ByteBlock value;

    public Checksum(ByteBlock value) {
        value.checkLength(Length);
        this.value = value;
    }

    public Checksum(String checksumString) {
        checkChecksumStringLength(checksumString);
        byte[] value = new byte[Length];
        for (int index = 0; index < Length; ++index) {
            int position = index * 2;
            value[index] = (byte) parseByte(checksumString, position);
        }
        this.value = new ByteBlock(value, Length);
    }

    private void checkChecksumStringLength(String checksumString) {
        if (checksumString.length() != Length * 2) {
            throw new IllegalArgumentException("Illegal checksum string [" + checksumString + "]" +
                    " of length " + checksumString.length() + " - expected: " + Length * 2);
        }
    }

    private int parseByte(String string, int position) {
        return Integer.parseInt(string.substring(position, position + 2), 16);
    }

    public ByteBlock getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Checksum{" +
                "value=" + value +
                '}';
    }
}
