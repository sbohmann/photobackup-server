package at.yeoman.photobackup.server.gallery;

import at.yeoman.photobackup.server.api.ResourceDescription;
import at.yeoman.photobackup.server.core.Core;
import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
        core.getAssetDescriptions();
    }
}
