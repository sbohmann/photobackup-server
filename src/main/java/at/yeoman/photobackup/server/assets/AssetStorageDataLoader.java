package at.yeoman.photobackup.server.assets;

import at.yeoman.photobackup.server.core.Assets;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

// TODO use or delete
class AssetStorageDataLoader {
    private static final Logger logger = LoggerFactory.getLogger(AssetStorage.class);
    private static final String StoragePath = "assets.json";

    private ObjectMapper objectMapper = new ObjectMapper();

    private Assets result;

    Assets run() {
        if (loadDataFromFile()) {
            return result;
        } else {
            return null;
        }
    }

    private boolean loadDataFromFile() {
        File storageFile = new File(StoragePath);
        if (storageFile.isFile()) {
            return loadDataFromExistingFile(storageFile);
        } else {
            return false;
        }
    }

    private boolean loadDataFromExistingFile(File storageFile) {
        try {
            loadDataFromFileOrThrow(storageFile);
            return true;
        } catch (IOException exception) {
            logger.error("Unable to load data from existing file [" + storageFile.getAbsolutePath() + "]");
            return false;
        }
    }

    private void loadDataFromFileOrThrow(File storageFile) throws IOException {
        InputStream in = new FileInputStream(storageFile);
        result = objectMapper.readerFor(Assets.class).readValue(in);
    }
}
