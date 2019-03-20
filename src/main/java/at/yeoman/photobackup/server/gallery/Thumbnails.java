package at.yeoman.photobackup.server.gallery;

import at.yeoman.photobackup.server.Directories;
import at.yeoman.photobackup.server.assets.Checksum;
import at.yeoman.photobackup.server.assets.ResourceDescription;
import at.yeoman.photobackup.server.core.Core;
import at.yeoman.photobackup.server.core.ResourceClassification;
import at.yeoman.photobackup.server.imageMagick.ImageMagick;
import at.yeoman.photobackup.server.io.FileContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class Thumbnails {
    private static final Logger log = LoggerFactory.getLogger(Thumbnails.class);
    private static final Pattern thumbnailFileNamePattern = Pattern.compile("([0-9a-fA-F]{128}).jpg");
    
    private final Core core;
    
    private Map<Checksum, File> thumbnailForChecksum = new HashMap<>();
    private LinkedBlockingQueue<Checksum> backgroundCreationQueue = new LinkedBlockingQueue<>();
    
    @Autowired
    Thumbnails(Core core) throws IOException {
        this.core = core;
        readExistingThumbnails();
        new Thread(this::handleBackgroundCreationQueue).start();
    }
    
    private void readExistingThumbnails() throws IOException {
        for (File file : listFilesInThumbnailsDirectory()) {
            Matcher matcher = thumbnailFileNamePattern.matcher(file.getName());
            if (matcher.matches()) {
                addFile(file, matcher.group(1));
            }
        }
    }
    
    private File[] listFilesInThumbnailsDirectory() throws IOException {
        File[] result = Directories.Thumbnails.listFiles();
        if (result == null) {
            throw new IOException("Unable to list content of directory " + Directories.Thumbnails);
        }
        return result;
    }
    
    private void addFile(File file, String checksumString) {
        try {
            addFileForCalculatedChecksum(file, checksumString);
        } catch (Exception error) {
            log.error(error.getMessage(), error);
        }
    }
    
    private void handleBackgroundCreationQueue() {
        log.info("Enqueuing existing resources for background thumbnail creation...");
        enqueueExistingResourceChecksums();
        log.info("Finished enqueuing existing resources for background thumbnail creation. Listening for new checksums.");
        while (!Thread.interrupted()) {
            try {
                Checksum checksum = backgroundCreationQueue.take();
                if (potentialImageResource(checksum)) {
                    createIfMissing(checksum);
                }
            } catch (InterruptedException interrupted) {
                log.info("Thumbnails background creation thread interrupted via exception", interrupted);
            } catch (Exception error) {
                log.error(error.getMessage(), error);
            }
        }
        log.info("Thumbnails background creation thread stopping.");
    }
    
    private boolean potentialImageResource(Checksum checksum) {
        List<ResourceDescription> resourcesForChecksum = core.getAssets().resourcesForChecksum.get(checksum);
        if (resourcesForChecksum == null || resourcesForChecksum.isEmpty()) {
            return true;
        }
        for (ResourceDescription resource : resourcesForChecksum) {
            if (!ResourceClassification.nonImageName(resource.name)) {
                return true;
            }
        }
        return false;
    }
    
    private void enqueueExistingResourceChecksums() {
        try {
            for (File file : listResourceFiles()) {
                Checksum checksum = new Checksum(file.getName());
                backgroundCreationQueue.put(checksum);
            }
            log.info("Enqueued " + backgroundCreationQueue.size() +
                    " existing resources for background thumbnail creation");
        } catch (Exception error) {
            log.error("Error while enqueuing existing resources for background thumbnail creation", error);
        }
    }
    
    private File[] listResourceFiles() {
        return Directories.Photos.listFiles(this::isResourceFile);
    }
    
    private boolean isResourceFile(File file) {
        return Checksum
                .StringPattern
                .matcher(file.getName())
                .matches();
    }
    
    private void addFileForCalculatedChecksum(File file, String checksumString) {
        Checksum checksum = new Checksum(checksumString);
        addFileForChecksum(file, checksum);
    }
    
    private void addFileForChecksum(File file, Checksum checksum) {
        File previousValue = thumbnailForChecksum.put(checksum, file);
        if (previousValue != null) {
            throw new RuntimeException("Double occurence of " + checksum);
        }
    }
    
    synchronized public byte[] get(Checksum checksum) throws IOException {
        File resultFile = thumbnailForChecksum.get(checksum);
        if (resultFile != null && resultFile.isFile()) {
            return FileContent.read(resultFile);
        } else {
            return createThumbnail(checksum);
        }
    }
    
    synchronized public void createInBackgroundIfMissing(Checksum checksum) {
        if (checksum != null) {
            if (!backgroundCreationQueue.offer(checksum)) {
                log.error("Unable to enqueue checksum for background thumbnail creation: " + checksum);
            }
        }
    }
    
    synchronized private void createIfMissing(Checksum checksum) {
        File existingFile = thumbnailForChecksum.get(checksum);
        if (existingFile == null || !existingFile.isFile()) {
            createThumbnail(checksum);
        }
    }
    
    private byte[] createThumbnail(Checksum checksum) {
        try {
            File originalImageFile = new File(Directories.Photos, checksum.toRawString());
            if (originalImageFile.isFile()) {
                return createAndWriteThumbnailContent(checksum, originalImageFile);
            }
        } catch (Exception error) {
            log.error(error.getMessage(), error);
        }
        return null;
    }
    
    private byte[] createAndWriteThumbnailContent(Checksum checksum, File originalImageFile) throws IOException {
        byte[] originalImageFileContent = FileContent.read(originalImageFile);
        byte[] thumbnailContent = ImageMagick.convertToJpegWithMaximumSize(originalImageFileContent, 200, 200);
        if (thumbnailContent == null) {
            log.info("Thumbnail creation failed (null) for " + resourceType(checksum) + " resource " + checksum.toRawString());
            return null;
        }
        if (thumbnailContent.length == 0) {
            log.info("Thumbnail creation failed (empty data) for " + resourceType(checksum) + " resource " + checksum.toRawString());
            return null;
        }
        writeThumbnailFile(checksum, thumbnailContent);
        return thumbnailContent;
    }
    
    private String resourceType(Checksum checksum) {
        List<ResourceDescription> resources = core.getAssets().resourcesForChecksum.get(checksum);
        if (resources == null || resources.isEmpty()) {
            return "unknown";
        } else {
            return resources
                    .stream()
                    .flatMap(this::fileType)
                    .distinct()
                    .collect(Collectors.joining(", "));
        }
    }
    
    private Stream<String> fileType(ResourceDescription resource) {
        Optional<String> result = ResourceClassification.fileType(resource.name);
        return result.map(Stream::of).orElseGet(Stream::empty);
    }
    
    private void writeThumbnailFile(Checksum checksum, byte[] thumbnailContent) {
        File thumbnailFile = new File(Directories.Thumbnails, checksum.toRawString() + ".jpg");
        checkPath(thumbnailFile);
        try (OutputStream out = new FileOutputStream(thumbnailFile)) {
            log.info("Writing thumbnail content to file [" + thumbnailFile.getCanonicalPath() + "]");
            out.write(thumbnailContent);
            thumbnailForChecksum.put(checksum, thumbnailFile);
        } catch (Exception error) {
            log.error(error.getMessage(), error);
        }
    }
    
    private void checkPath(File thumbnailFile) {
        if (!thumbnailFile.toPath().startsWith(Directories.Thumbnails.toPath())) {
            throw new RuntimeException("Not inside " + Directories.Thumbnails.getAbsolutePath() +
                    ": " + thumbnailFile.getAbsolutePath());
        }
        if (thumbnailFile.toPath().startsWith(Directories.Photos.toPath())) {
            throw new RuntimeException("Inside " + Directories.Photos.getAbsolutePath() +
                    ": " + thumbnailFile.getAbsolutePath());
        }
    }
}
