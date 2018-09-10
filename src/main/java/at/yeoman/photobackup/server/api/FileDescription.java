package at.yeoman.photobackup.server.api;

import java.time.Instant;

public class FileDescription {
    private final Checksum checksum;
    private final long size;
    private final DeviceId device;
    private final String name;
    private final Instant creationTime;

    public FileDescription(Checksum checksum, long size, DeviceId device, String name, Instant creationTime) {
        this.checksum = checksum;
        this.size = size;
        this.device = device;
        this.name = name;
        this.creationTime = creationTime;
    }

    public Checksum getChecksum() {
        return checksum;
    }

    public long getSize() {
        return size;
    }

    public DeviceId getDevice() {
        return device;
    }

    public String getName() {
        return name;
    }

    public Instant getCreationTime() {
        return creationTime;
    }

    @Override
    public String toString() {
        return "FileDescription{" +
                "checksum=" + checksum +
                ", size=" + size +
                ", device=" + device +
                ", name='" + name + '\'' +
                ", creationTime=" + creationTime +
                '}';
    }
}
