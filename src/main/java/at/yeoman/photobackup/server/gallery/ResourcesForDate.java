package at.yeoman.photobackup.server.gallery;

import at.yeoman.photobackup.server.assets.ResourceDescription;
import at.yeoman.photobackup.server.core.Core;
import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.util.List;

class ResourcesForDate {
    List<ResourceDescription> result;

    private final Core core;
    private final LocalDate date;

    ResourcesForDate(Core core, @Nullable LocalDate date) {
        this.core = core;
        this.date = date;
        determineResult();
    }

    private void determineResult() {
        //core.getAssetDescriptions();
    }
}
