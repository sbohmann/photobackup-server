package at.yeoman.photobackup.server.api;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

public class AssetDescription {
    // TODO location information from PHAsset
    // TODO creation timestamp, name, tags, &c.

    private String name;
    private long creationDateMs;
    private List<ResourceDescription> resourceDescriptions;

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

    public List<ResourceDescription> getResourceDescriptions() {
        return resourceDescriptions;
    }

    public void setResourceDescriptions(List<ResourceDescription> resourceDescriptions) {
        this.resourceDescriptions = resourceDescriptions;
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
