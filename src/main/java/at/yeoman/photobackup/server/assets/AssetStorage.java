package at.yeoman.photobackup.server.assets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AssetStorage {
    private final Logger logger = LoggerFactory.getLogger(AssetStorage.class);

    private Assets data;

    AssetStorage() {
        data = new AssetStorageDataLoader().run();
    }
}
