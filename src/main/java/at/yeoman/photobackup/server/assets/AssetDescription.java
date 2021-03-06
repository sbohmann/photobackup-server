package at.yeoman.photobackup.server.assets;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Objects;

@Immutable
public final class AssetDescription {
    // TODO location information from PHAsset
    // TODO creation timestamp, name, tags, &c.
    
    public final String name;
    public final long creationDateMs;
    public final long modificationDateMs;
    public final ImmutableList<ResourceDescription> resourceDescriptions;
    
    @JsonCreator
    public AssetDescription(
            @JsonProperty("name") String name,
            @JsonProperty("creationDateMs") long creationDateMs,
            @JsonProperty("modificationDateMs") long modificationDateMs,
            @JsonProperty("resourceDescriptions") List<ResourceDescription> resourceDescriptions) {
        this.name = name;
        this.creationDateMs = creationDateMs;
        this.modificationDateMs = modificationDateMs;
        this.resourceDescriptions = ImmutableList.copyOf(resourceDescriptions);
    }
    
    public String getName() {
        return name;
    }
    
    public long getCreationDateMs() {
        return creationDateMs;
    }

    public long getModificationDateMs() {
        return modificationDateMs;
    }

    public List<ResourceDescription> getResourceDescriptions() {
        return resourceDescriptions;
    }
    
    @Override
    public String toString() {
        return "AssetDescription{" +
                "name='" + name + '\'' +
                ", creationDateMs=" + creationDateMs +
                ", resourceDescriptions=" + resourceDescriptions +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssetDescription that = (AssetDescription) o;
        return creationDateMs == that.creationDateMs &&
                Objects.equals(name, that.name) &&
                Objects.equals(resourceDescriptions, that.resourceDescriptions);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, creationDateMs, resourceDescriptions);
    }
}
