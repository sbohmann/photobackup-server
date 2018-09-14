package at.yeoman.photobackup.server.api;

public class ResourceDescription {
    private Checksum checksum;
    private long size;
    private String name;
    private long creationDateMs;

    public Checksum getChecksum() {
        return checksum;
    }

    public void setChecksum(Checksum checksum) {
        this.checksum = checksum;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCreationDateMs() {
        return creationDateMs;
    }

    public void setCreationDateMs(long creationDateMs) {
        this.creationDateMs = creationDateMs;
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
