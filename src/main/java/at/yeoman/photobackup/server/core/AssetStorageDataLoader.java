package at.yeoman.photobackup.server.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;

class AssetStorageDataLoader {
    private static final Logger log = LoggerFactory.getLogger(AssetStorageDataLoader.class);
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
        } catch (IOException error) {
            log.error("Unable to load data from existing file [" + storageFile.getAbsolutePath() + "]", error);
        }
    }

    @SuppressWarnings("unused")
    private void loadDataFromFileOrThrow() throws IOException {
        // TODO find a reasonable way to lock the file - RandomAccessFile is way too slow
        result = objectMapper.readerFor(Assets.class).readValue(storageFile);
    }
}
