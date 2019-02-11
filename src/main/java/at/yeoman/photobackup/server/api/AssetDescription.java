package at.yeoman.photobackup.server.api;

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

    public AssetDescription(String name, long creationDateMs, List<ResourceDescription> resourceDescriptions) {
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
