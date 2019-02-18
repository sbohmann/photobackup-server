package at.yeoman.photobackup.server.core;

import at.yeoman.photobackup.server.assets.AssetDescription;
import at.yeoman.photobackup.server.assets.Checksum;
import at.yeoman.photobackup.server.assets.ResourceDescription;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Assets {
    public final ImmutableList<AssetDescription> assets;
    @JsonIgnore public final ImmutableMap<Checksum, ImmutableList<AssetDescription>> assetsForChecksum;
    @JsonIgnore public final ImmutableMap<Checksum, ImmutableList<ResourceDescription>> resourcesForChecksum;
    @JsonIgnore public final ImmutableSet<AssetDescription> knownAssets;

    public Assets(ImmutableList<AssetDescription> assets,
                  ImmutableMap<Checksum, ImmutableList<AssetDescription>> assetsForChecksum, ImmutableMap<Checksum, ImmutableList<ResourceDescription>> resourcesForChecksum) {
        this.assets = assets;
        this.assetsForChecksum = assetsForChecksum;
        this.resourcesForChecksum = resourcesForChecksum;
        knownAssets = ImmutableSet.copyOf(assets);
    }

    @JsonCreator
    public Assets(@JsonProperty("assets") List<AssetDescription> assets) {
        this.assets = ImmutableList.copyOf(assets);
        AssetsAndResourcesForChecksum assetsAndresourcesForChecksum = new AssetsAndResourcesForChecksum(assets);
        assetsForChecksum = assetsAndresourcesForChecksum.assetsForChecksum;
        resourcesForChecksum = assetsAndresourcesForChecksum.resourcesForChecksum;
        knownAssets = ImmutableSet.copyOf(assets);
    }

    public Assets plus(List<AssetDescription> newAssets) {
        if (newAssets.isEmpty()) {
            return this;
        }
        List<AssetDescription> filtered = filterOutKnownAssets(newAssets);
        if (filtered.isEmpty()) {
            return this;
        }
        ArrayList<AssetDescription> combined = concatenate(filtered);
        return new Assets(combined);
    }

    private List<AssetDescription> filterOutKnownAssets(List<AssetDescription> newAssets) {
        return newAssets.stream()
                    .filter(asset -> !knownAssets.contains(asset))
                    .collect(Collectors.toList());
    }

    private ArrayList<AssetDescription> concatenate(List<AssetDescription> newAssets) {
        ArrayList<AssetDescription> combined =
                new ArrayList<>(assets.size() + newAssets.size());
        Set<AssetDescription> set = new HashSet<>();
        addMissing(assets, combined, set);
        addMissing(newAssets, combined, set);
        return combined;
    }

    private void addMissing(List<AssetDescription> source, List<AssetDescription> result, Set<AssetDescription> set) {
        for (AssetDescription asset : source) {
            if (set.add(asset)) {
                result.add(asset);
            }
        }
    }
}
