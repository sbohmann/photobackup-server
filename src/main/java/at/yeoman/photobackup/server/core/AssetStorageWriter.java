package at.yeoman.photobackup.server.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;

import static at.yeoman.photobackup.server.core.Assets.StorageFile;

class AssetStorageWriter {
    private static final Logger log = LoggerFactory.getLogger(AssetStorageWriter.class);
    
    private Assets assets;
    
    private ObjectMapper objectMapper = new ObjectMapper();
    
    AssetStorageWriter(Assets assets) {
        this.assets = assets;
    }
    
    void run() {
        writeAssetDataFile();
    }
    
    private void writeAssetDataFile() {
        try {
            writeDataToFileOrThrow();
        } catch (IOException error) {
            log.error("Unable to write data to file [" + StorageFile.getAbsolutePath() + "]", error);
        }
    }
    
    // TODO create target if necesssary, write to intermediary file, then replace, all with both locked
    @SuppressWarnings("unused")
    private void writeDataToFileOrThrow() throws IOException {
        try (FileOutputStream out = new FileOutputStream(StorageFile)) {
            out.getChannel().lock();
            objectMapper.writerFor(Assets.class).writeValue(out, assets);
            //finishAndSync(out, log, storageFile);
            log.info("finished writing assets.");
        }
    }
}
