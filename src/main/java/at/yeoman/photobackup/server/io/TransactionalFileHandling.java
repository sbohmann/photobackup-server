package at.yeoman.photobackup.server.io;

import org.slf4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.SyncFailedException;

public class TransactionalFileHandling {
    public static void finishAndSync(FileOutputStream out, Logger log, File target) throws IOException {
        out.getChannel().force(true);
        try {
            out.getFD().sync();
        } catch (SyncFailedException exception) {
            log.debug("Unable to sync file [" + target.getCanonicalPath() + "]", exception);
        }
    }
}
