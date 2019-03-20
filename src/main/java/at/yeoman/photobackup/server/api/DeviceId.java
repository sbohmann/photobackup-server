package at.yeoman.photobackup.server.api;

import at.yeoman.photobackup.server.primtive.ByteBlock;

public class DeviceId {
    private static final int Length = 16;
    
    private final ByteBlock value;
    
    public DeviceId(ByteBlock value) {
        value.checkLength(Length);
        this.value = value;
    }
    
    public ByteBlock getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return "DeviceId{" +
                "value=" + value +
                '}';
    }
}
