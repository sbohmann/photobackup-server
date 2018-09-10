package at.yeoman.photobackup.server.api;

import at.yeoman.photobackup.server.primtive.ByteBlock;

public class Checksum {
    private static final int Length = 512;

    private ByteBlock data;

    public Checksum(ByteBlock data) {
        data.checkLength(Length);
        this.data = data;
    }

    public ByteBlock getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Checksum{" +
                "data=" + data +
                '}';
    }
}
