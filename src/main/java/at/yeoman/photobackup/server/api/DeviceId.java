package at.yeoman.photobackup.server.api;

import at.yeoman.photobackup.server.primtive.ByteBlock;

public class DeviceId {
    private static final int Length = 128;

    private final ByteBlock data;

    public DeviceId(ByteBlock data) {
        data.checkLength(Length);
        this.data = data;
    }

    public ByteBlock getData() {
        return data;
    }

    @Override
    public String toString() {
        return "DeviceId{" +
                "data=" + data +
                '}';
    }
}
