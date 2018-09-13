package at.yeoman.photobackup.server.api;

import com.fasterxml.jackson.annotation.JsonCreator;

public class ImageDescription {
    // TODO location information from PHAsset
    // TODO creation timestamp, name, tags, &c.

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ImageDescription{" +
                "name='" + name + '\'' +
                '}';
    }
}
