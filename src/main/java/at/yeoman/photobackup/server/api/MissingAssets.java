package at.yeoman.photobackup.server.api;

import at.yeoman.photobackup.server.assets.Checksum;

import java.util.List;

public class MissingAssets {
    List<Checksum> missingAssetChecksums;

    public List<Checksum> getMissingAssetChecksums() {
        return missingAssetChecksums;
    }

    public void setMissingAssetChecksums(List<Checksum> missingAssetChecksums) {
        this.missingAssetChecksums = missingAssetChecksums;
    }

    @Override
    public String toString() {
        return "MissingAssets{" +
                "missingAssetChecksums=" + missingAssetChecksums +
                '}';
    }
}
