package at.yeoman.photobackup.server.api;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class ResourceDescription {
    public final Checksum checksum;
    public final long size;
    public final String name;
    public final long creationDateMs;

    public ResourceDescription(Checksum checksum, long size, String name, long creationDateMs) {
        this.checksum = checksum;
        this.size = size;
        this.name = name;
        this.creationDateMs = creationDateMs;
    }

    public Checksum getChecksum() {
        return checksum;
    }

    public long getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    public long getCreationDateMs() {
        return creationDateMs;
    }

    @Override
    public String toString() {
        return "ResourceDescription{" +
                "checksum=" + checksum +
                ", size=" + size +
                ", name='" + name + '\'' +
                ", creationDateMs=" + creationDateMs +
                '}';
    }
}
