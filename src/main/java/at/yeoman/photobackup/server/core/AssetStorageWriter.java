package at.yeoman.photobackup.server.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;

import static at.yeoman.photobackup.server.io.TransactionalFileHandling.finishAndSync;

class AssetStorageWriter {
    private static final Logger log = LoggerFactory.getLogger(AssetStorageWriter.class);
    private static final String StoragePath = "assets.json";

    private Assets assets;

    private ObjectMapper objectMapper = new ObjectMapper();
    private File storageFile;

    AssetStorageWriter(Assets assets) {
        this.assets = assets;
    }

    void run() {
        storageFile = new File(StoragePath);
        writeAssetDataFile();
    }

    private void writeAssetDataFile() {
        try {
            writeDataToFileOrThrow();
        } catch (IOException error) {
            log.error("Unable to write data to file [" + storageFile.getAbsolutePath() + "]", error);
        }
    }

    // TODO create target if necesssary, write to intermediary file, then replace, all with both locked
    @SuppressWarnings("unused")
    private void writeDataToFileOrThrow() throws IOException {
        try (FileOutputStream out = new FileOutputStream(storageFile)) {
            out.getChannel().lock();
            objectMapper.writerFor(Assets.class).writeValue(out, assets);
            //finishAndSync(out, log, storageFile);
            log.info("finished writing assets.");
        }
    }
}
