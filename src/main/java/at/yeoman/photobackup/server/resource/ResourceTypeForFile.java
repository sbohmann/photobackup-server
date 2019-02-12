package at.yeoman.photobackup.server.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

// TODO use or delete
public class ResourceTypeForFile {
    private static Logger logger = LoggerFactory.getLogger(ResourceTypeForFile.class);

    private File file;

    public static ResourceType get(File file) {
        return new ResourceTypeForFile(file).run();
    }

    private ResourceTypeForFile(File file) {
        this.file = file;
    }

    private ResourceType run() {
        try {
            return probeContentType();
        } catch (IOException exception) {
            logger.error("Unable to probe content type of file [" + file.getAbsolutePath() + "]");
            return ResourceType.Unknown;
        }
    }

    private ResourceType probeContentType() throws IOException {
        String rawContentType = Files.probeContentType(file.toPath());
        if (rawContentType != null) {
            logger.info(file.getAbsolutePath() + " -> " + rawContentType);
        }
        return ResourceType.Unknown;
    }
}
