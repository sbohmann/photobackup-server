package at.yeoman.photobackup.server.gallery;

import at.yeoman.photobackup.server.assets.AssetDescription;
import at.yeoman.photobackup.server.core.Core;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

class AssetsForMonth {
    List<AssetDescription> result;
    
    private final Core core;
    private final int year;
    private final int month;
    
    AssetsForMonth(Core core, int year, int month) {
        this.core = core;
        this.year = year;
        this.month = month;
        determineResult();
    }
    
    private void determineResult() {
        result = core
                .getAssets()
                .assets
                .stream()
                .filter(asset -> inRange(asset.creationDateMs))
                .collect(Collectors.toList());
        result.sort(Comparator.comparing(asset -> asset.creationDateMs));
    }
    
    private boolean inRange(long creationDateMs) {
        LocalDate date = LocalDateTime
                .ofInstant(Instant.ofEpochMilli(creationDateMs), ZoneOffset.UTC)
                .toLocalDate();
        return date.getYear() == year && date.getMonthValue() == month;
    }
}
