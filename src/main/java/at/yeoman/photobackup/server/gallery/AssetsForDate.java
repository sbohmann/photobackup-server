package at.yeoman.photobackup.server.gallery;

import at.yeoman.photobackup.server.assets.AssetDescription;
import at.yeoman.photobackup.server.core.Core;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

class AssetsForDate {
    List<AssetDescription> result;
    
    private final Core core;
    private final LocalDate date;
    
    AssetsForDate(Core core, @Nullable LocalDate date) {
        this.core = core;
        this.date = date;
        determineResult();
    }
    
    private void determineResult() {
        if (date != null) {
            result = core
                    .getAssets()
                    .assets
                    .stream()
                    .filter(asset -> inRange(asset.creationDateMs))
                    .collect(Collectors.toList());
        } else {
            result = new ArrayList<>(core.getAssets().assets);
        }
        result.sort(Comparator.comparing(asset -> asset.creationDateMs));
    }
    
    private boolean inRange(long creationDateMs) {
        return LocalDateTime
                .ofInstant(Instant.ofEpochMilli(creationDateMs), ZoneOffset.UTC)
                .toLocalDate()
                .equals(date);
    }
}
