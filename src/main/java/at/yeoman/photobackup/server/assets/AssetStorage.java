package at.yeoman.photobackup.server.assets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AssetStorage {
    private final Logger logger = LoggerFactory.getLogger(AssetStorage.class);

    private AssetStorageWriter writer;

    private Assets data;

    AssetStorage() {
        loadAssetData();
    }

    private void loadAssetData() {
        data = new AssetStorageDataLoader().run();
        if (data == null) {
            data = new AssetReportCollector().run();
        }
    }
}

