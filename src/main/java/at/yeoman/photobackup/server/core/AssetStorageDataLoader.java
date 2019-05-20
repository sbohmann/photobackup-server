package at.yeoman.photobackup.server.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;

import static at.yeoman.photobackup.server.core.Assets.StorageFile;

class AssetStorageDataLoader {
    private static final Logger log = LoggerFactory.getLogger(AssetStorageDataLoader.class);
    
    private ObjectMapper objectMapper = new ObjectMapper();
    
    @Nullable
    public Assets result;
    
    AssetStorageDataLoader() {
        if (StorageFile.isFile()) {
            loadDataFromExistingFile();
        }
    }
    
    private void loadDataFromExistingFile() {
        try {
            loadDataFromFileOrThrow();
        } catch (IOException error) {
            log.error("Unable to load data from existing file [" + StorageFile.getAbsolutePath() + "]", error);
        }
    }
    
    @SuppressWarnings("unused")
    private void loadDataFromFileOrThrow() throws IOException {
        // TODO find a reasonable way to lock the file - RandomAccessFile is way too slow
        result = objectMapper.readerFor(Assets.class).readValue(StorageFile);
    }
}
