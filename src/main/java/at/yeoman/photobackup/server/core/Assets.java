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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Assets {
    public final ImmutableList<AssetDescription> assets;
    @JsonIgnore public final ImmutableMap<Checksum, ImmutableList<AssetDescription>> assetsForResource;
    @JsonIgnore public final ImmutableSet<AssetDescription> knownAssets;

    public Assets(ImmutableList<AssetDescription> assets,
                  ImmutableMap<Checksum, ImmutableList<AssetDescription>> assetsForResource) {
        this.assets = assets;
        this.assetsForResource = assetsForResource;
        knownAssets = ImmutableSet.copyOf(assets);
    }

    @JsonCreator
    public Assets(@JsonProperty("assets") List<AssetDescription> assets) {
        this.assets = ImmutableList.copyOf(assets);
        assetsForResource = createAssetsForResource(assets);
        knownAssets = ImmutableSet.copyOf(assets);
    }

    private ImmutableMap<Checksum, ImmutableList<AssetDescription>> createAssetsForResource(List<AssetDescription> assets) {
        Map<Checksum, List<AssetDescription>> result = new HashMap<>();
        for (AssetDescription asset : assets) {
            for (ResourceDescription resource : asset.getResourceDescriptions()) {
                List<AssetDescription> assetsForResource = listForKey(result, resource.checksum);
                assetsForResource.add(asset);
            }
        }
        return ImmutableMap.copyOf(
                withImmutableLists(result));
    }

    private static <K,E> List<E> listForKey(Map<K, List<E>> map, K key) {
        return map.computeIfAbsent(key, x -> new ArrayList<>());
    }

    private Map<Checksum, ImmutableList<AssetDescription>> withImmutableLists(Map<Checksum, List<AssetDescription>> result) {
        return result.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> ImmutableList.copyOf(entry.getValue())));
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
        combined.addAll(assets);
        combined.addAll(newAssets);
        return combined;
    }
}
