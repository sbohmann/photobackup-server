package at.yeoman.photobackup.server.assets;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.concurrent.Immutable;
import java.util.Objects;

@Immutable
public final class ResourceDescription {
    public final Checksum checksum;
    public final long size;
    public final String name;
    public final long creationDateMs;
    
    @JsonCreator
    public ResourceDescription(
            @JsonProperty("checksum") Checksum checksum,
            @JsonProperty("size") long size,
            @JsonProperty("name") String name,
            @JsonProperty("creationDateMs") long creationDateMs) {
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
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResourceDescription that = (ResourceDescription) o;
        return size == that.size &&
                creationDateMs == that.creationDateMs &&
                Objects.equals(checksum, that.checksum) &&
                Objects.equals(name, that.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(checksum, size, name, creationDateMs);
    }
}
