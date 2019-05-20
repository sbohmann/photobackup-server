package at.yeoman.photobackup.server.core;

import at.yeoman.photobackup.server.Directories;
import at.yeoman.photobackup.server.api.AssetReport;
import at.yeoman.photobackup.server.configuration.CoreConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class Core {
    private final Logger log = LoggerFactory.getLogger(Core.class);
    
    private CoreConfiguration configuration;
    private Assets assets;
    
    @Autowired
    Core(CoreConfiguration configuration) {
        this.configuration = configuration;
        assets = new AssetsFromFileSystem(configuration).result;
        loadAssetData();
    }
    
    synchronized public Assets getAssets() {
        return assets;
    }
    
    synchronized public void reportAssets(AssetReport report) {
        assets = assets.plus(report.getDescriptions());
        writeAssetReport(report);
        writeAssetData();
    }
    
    private void writeAssetReport(AssetReport report) {
        try {
            assets = assets.plus(report.getDescriptions());
            File file = new File(Directories.Assets, IsoStringForInstant.now() + ".json");
            new ObjectMapper().writeValue(file, report);
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
        }
    }
    
    private void writeAssetData() {
        try {
            new AssetStorageWriter(assets).run();
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
        }
    }
    
    synchronized private void loadAssetData() {
        log.info("Reading assets...");
        assets = new AssetStorageDataLoader().result;
        if (assets == null) {
            log.info("No result, collecting assets...");
            assets = new AssetReportCollector().result;
            log.info("Writing assets file...");
            new AssetStorageWriter(assets).run();
        }
        log.info("Finished reading assets.");
        log.info(assets.assets.size() + " assets, " + assets.assetsForChecksum.size() + " known resources.");
    }
}
