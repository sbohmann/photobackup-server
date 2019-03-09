package at.yeoman.photobackup.server.assets;

import at.yeoman.photobackup.server.primtive.ByteBlock;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.annotation.concurrent.Immutable;
import java.util.Objects;
import java.util.regex.Pattern;

@Immutable
@JsonSerialize(using = ChecksumSerializer.class)
@JsonDeserialize(using = ChecksumDeserializer.class)
public final class Checksum {
    public static final Pattern StringPattern = Pattern.compile("[0-9a-fA-F]{128}");
    
    private static final int Length = 64;
    
    private final ByteBlock value;
    
    @JsonCreator
    public Checksum(@JsonProperty("value") ByteBlock value) {
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
    
    public Checksum(byte[] value) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Checksum checksum = (Checksum) o;
        return Objects.equals(value, checksum.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return "Checksum{" +
                "value=" + value +
                '}';
    }
    
    public String toRawString() {
        return value.toRawString();
    }
}
