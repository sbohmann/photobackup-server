package at.yeoman.photobackup.server.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;

class AssetStorageWriter {
    private static final Logger logger = LoggerFactory.getLogger(AssetStorageWriter.class);
    private static final String StoragePath = "assets.json";

    private Assets assets;

    private ObjectMapper objectMapper = new ObjectMapper();
    private File storageFile;

    AssetStorageWriter(Assets assets) {
        this.assets = assets;
    }

    void run() {
        storageFile = new File(StoragePath);
        if (storageFile.isFile()) {
            loadDataFromExistingFile();
        }
    }

    private void loadDataFromExistingFile() {
        try {
            loadDataFromFileOrThrow();
        } catch (IOException exception) {
            logger.error("Unable to load data from existing file [" + storageFile.getAbsolutePath() + "]");
        }
    }

    @SuppressWarnings("unused")
    private void loadDataFromFileOrThrow() throws IOException {
        FileOutputStream out = new FileOutputStream(storageFile);
        try (FileLock lock = out.getChannel().lock()) {
            objectMapper.writerFor(Assets.class).writeValue(out, assets);
        }
    }
}
