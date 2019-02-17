package at.yeoman.photobackup.server.api;

import at.yeoman.photobackup.server.assets.AssetDescription;

import java.util.List;

public class AssetReport {
    private List<AssetDescription> descriptions;

    public List<AssetDescription> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<AssetDescription> descriptions) {
        this.descriptions = descriptions;
    }

    @Override
    public String toString() {
        return "AssetReport{" +
                "descriptions=" + descriptions +
                '}';
    }
}
