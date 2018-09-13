package at.yeoman.photobackup.server.api;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

public class AssetReport {
    private List<ImageDescription> descriptions;

    public List<ImageDescription> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<ImageDescription> descriptions) {
        this.descriptions = descriptions;
    }

    @Override
    public String toString() {
        return "AssetReport{" +
                "descriptions=" + descriptions +
                '}';
    }
}
