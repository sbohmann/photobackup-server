package at.yeoman.photobackup.server.assets;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import javax.annotation.concurrent.Immutable;
import java.util.List;

@Immutable
public final class AssetDescription {
    // TODO location information from PHAsset
    // TODO creation timestamp, name, tags, &c.

    public final String name;
    public final long creationDateMs;
    public final ImmutableList<ResourceDescription> resourceDescriptions;

    @JsonCreator
    public AssetDescription(
            @JsonProperty("name") String name,
            @JsonProperty("creationDateMs") long creationDateMs,
            @JsonProperty("resourceDescriptions") List<ResourceDescription> resourceDescriptions) {
        this.name = name;
        this.creationDateMs = creationDateMs;
        this.resourceDescriptions = ImmutableList.copyOf(resourceDescriptions);
    }

    public String getName() {
        return name;
    }

    public long getCreationDateMs() {
        return creationDateMs;
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
}
