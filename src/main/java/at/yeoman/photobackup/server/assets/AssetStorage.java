package at.yeoman.photobackup.server.assets;

import at.yeoman.photobackup.server.Directories;
import at.yeoman.photobackup.server.resource.ResourceTypeForFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;

@Component
public class AssetStorage {
    private final Logger logger = LoggerFactory.getLogger(AssetStorage.class);

    private AssetStorageWriter writer;

    private Assets data;

    AssetStorage() {
        scanResources();
        loadAssetData();
    }

    private void scanResources() {
        logger.info("Scanning resources...");
        File[] files = Directories.Photos.listFiles();
        Arrays
                .stream(files)
                .map(ResourceTypeForFile::get)
                .forEach(System.out::println);
    }

    private void loadAssetData() {
        data = new AssetStorageDataLoader().run();
        if (data == null) {
            data = new AssetReportCollector().run();
        }
    }
}
