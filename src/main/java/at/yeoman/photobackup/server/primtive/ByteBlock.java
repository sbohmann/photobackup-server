package at.yeoman.photobackup.server.primtive;

import javax.annotation.concurrent.Immutable;
import java.util.Arrays;
import java.util.Random;
import java.util.regex.Pattern;

@Immutable
public final class ByteBlock {
    public static final Pattern HexStringPattern = Pattern.compile("(?:[0-9a-fA-F]{2})+");

    private final byte[] value;

    public ByteBlock(byte[] value) {
        this.value = value.clone();
    }
    
    public ByteBlock(byte[] value, int expectedLength) {
        checkLength(value, expectedLength);
        this.value = value.clone();
    }

    public ByteBlock(int length, Random random) {
        value = new byte[length];
        random.nextBytes(value);
    }

    public ByteBlock(String checksumString, int length) {
        checkChecksumStringLength(checksumString, length);
        byte[] value = new byte[length];
        for (int index = 0; index < length; ++index) {
            int position = index * 2;
            value[index] = (byte) parseByte(checksumString, position);
        }
        this.value = value;
    }

    public ByteBlock(String hexString) {
        this(hexString, hexString.length() / 2);
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
    
    private void checkChecksumStringLength(String hexString, int length) {
        if (hexString.length() != length * 2) {
            throw new IllegalArgumentException("Illegal checksum string [" + hexString + "]" +
                    " of length " + hexString.length() + " - expected: " + length * 2);
        }
    }
    
    private int parseByte(String checksumString, int position) {
        return Integer.parseInt(checksumString.substring(position, position + 2), 16);
    }
    
    public String toRawString() {
        StringBuilder result = new StringBuilder(value.length * 2);
        for (byte byteValue : value) {
            byteValue &= 0xff;
            result.append(hexChar(byteValue >>> 4));
            result.append(hexChar(byteValue & 0x0f));
        }
        return result.toString();
    }
    
    private char hexChar(int nibble) {
        nibble &= 0x0f;
        if (nibble >= 0 && nibble <= 9) {
            return (char) ('0' + nibble);
        } else if (nibble >= 0x0a && nibble <= 0x0f) {
            return (char) ('A' + (nibble - 0x0a));
        } else {
            throw new IllegalArgumentException("nibble value: " + nibble);
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ByteBlock byteBlock = (ByteBlock) o;
        return Arrays.equals(value, byteBlock.value);
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }
    
    @Override
    public String toString() {
        return toRawString();
    }

    public byte[] rawCopy() {
        return value.clone();
    }
}
