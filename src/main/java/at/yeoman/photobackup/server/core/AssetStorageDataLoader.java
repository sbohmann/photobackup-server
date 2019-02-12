package at.yeoman.photobackup.server.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileLock;

class AssetStorageDataLoader {
    private static final Logger logger = LoggerFactory.getLogger(AssetStorageDataLoader.class);
    private static final String StoragePath = "assets.json";

    private ObjectMapper objectMapper = new ObjectMapper();
    private final File storageFile;

    @Nullable
    public Assets result;

    AssetStorageDataLoader() {
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
        FileInputStream in = new FileInputStream(storageFile);
        try (FileLock lock = in.getChannel().lock()) {
            result = objectMapper.readerFor(Assets.class).readValue(in);
        }
    }
}
