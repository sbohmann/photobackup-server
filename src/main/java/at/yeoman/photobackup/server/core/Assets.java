package at.yeoman.photobackup.server.core;

import at.yeoman.photobackup.server.api.AssetDescription;
import at.yeoman.photobackup.server.api.Checksum;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.List;

public class Assets {
    public final ImmutableList<AssetDescription> assets;
    public final ImmutableMap<Checksum, ImmutableList<AssetDescription>> assetsForResource;
    public final ImmutableMap<Checksum, AssetDescription> latestAssetForResource;
    public final ImmutableSet<AssetDescription> knownAssets;

    public Assets(ImmutableList<AssetDescription> assets,
                  ImmutableMap<Checksum, ImmutableList<AssetDescription>> assetsForResource,
                  ImmutableMap<Checksum, AssetDescription> latestAssetForResource) {
        this.assets = assets;
        this.assetsForResource = assetsForResource;
        this.latestAssetForResource = latestAssetForResource;
        knownAssets = ImmutableSet.copyOf(assets);
    }

    public Assets plus(List<AssetDescription> assets) {
        // TODO add
        return this;
    }
}
