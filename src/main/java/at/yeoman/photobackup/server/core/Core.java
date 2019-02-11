package at.yeoman.photobackup.server.core;

import at.yeoman.photobackup.server.Directories;
import at.yeoman.photobackup.server.api.AssetReport;
import at.yeoman.photobackup.server.api.Checksum;
import at.yeoman.photobackup.server.configuration.CoreConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class Core {
    private final Logger log = LoggerFactory.getLogger(Core.class);

    private CoreConfiguration configuration;
    private Assets assets;
    private List<Checksum> resources;

    @Autowired
    Core(CoreConfiguration configuration) {
        this.configuration = configuration;
        assets = new AssetsFromFileSystem(configuration).result;
    }

    synchronized public Assets getAssets() {
        return assets;
    }

    synchronized public void reportAssets(AssetReport report) {
        try {
            assets = assets.plus(report.getDescriptions());
            File file = new File(Directories.Assets, LocalDateTime.now().toString() + ".json");
            new ObjectMapper().writeValue(file, report);
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
        }
    }
}
