package at.yeoman.photobackup.server.core;

import at.yeoman.photobackup.server.assets.AssetDescription;
import at.yeoman.photobackup.server.assets.Checksum;
import at.yeoman.photobackup.server.assets.ResourceDescription;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class AssetsAndResourcesForChecksum {
    final ImmutableMap<Checksum, ImmutableList<AssetDescription>> assetsForChecksum;
    final ImmutableMap<Checksum, ImmutableList<ResourceDescription>> resourcesForChecksum;
    
    AssetsAndResourcesForChecksum(List<AssetDescription> assets) {
        final Map<Checksum, List<AssetDescription>> assetsForChecksum = new HashMap<>();
        final Map<Checksum, List<ResourceDescription>> resourcesForChecksum = new HashMap<>();
        for (AssetDescription asset : assets) {
            for (ResourceDescription resource : asset.getResourceDescriptions()) {
                List<AssetDescription> assetList = listForKey(assetsForChecksum, resource.checksum);
                List<ResourceDescription> resourceList = listForKey(resourcesForChecksum, resource.checksum);
                assetList.add(asset);
                resourceList.add(resource);
            }
        }
        this.assetsForChecksum = ImmutableMap.copyOf(withImmutableLists(assetsForChecksum));
        this.resourcesForChecksum = ImmutableMap.copyOf(withImmutableLists(resourcesForChecksum));
    }
    
    private static <K, E> List<E> listForKey(Map<K, List<E>> map, K key) {
        return map.computeIfAbsent(key, x -> new ArrayList<>());
    }
    
    private <E> Map<Checksum, ImmutableList<E>> withImmutableLists(Map<Checksum, List<E>> result) {
        return result
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> ImmutableList.copyOf(entry.getValue())));
    }
}
