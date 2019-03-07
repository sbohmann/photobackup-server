package at.yeoman.photobackup.server.gallery;

import at.yeoman.photobackup.server.Directories;
import at.yeoman.photobackup.server.assets.Checksum;
import at.yeoman.photobackup.server.assets.ResourceDescription;
import at.yeoman.photobackup.server.core.Core;
import at.yeoman.photobackup.server.core.ResourceClassification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static at.yeoman.photobackup.server.configuration.OperatingSystem.Windows;

@Component
public class Videos {
    private static final Logger log = LoggerFactory.getLogger(Videos.class);
    private static final Pattern videoFileNamePattern = Pattern.compile("([0-9a-fA-F]{128}).mp4");

    private final Core core;

    private Map<Checksum, File> videoForChecksum = new HashMap<>();
    private LinkedBlockingQueue<Checksum> backgroundCreationQueue = new LinkedBlockingQueue<>();

    @Autowired
    Videos(Core core) throws IOException {
        this.core = core;
        readExistingVideos();
        new Thread(this::handleBackgroundCreationQueue).start();
    }

    private void readExistingVideos() throws IOException {
        for (File file : listFilesInVideosDirectory()) {
            Matcher matcher = videoFileNamePattern.matcher(file.getName());
            if (matcher.matches()) {
                addFile(file, matcher.group(1));
            }
        }
    }

    private File[] listFilesInVideosDirectory() throws IOException {
        File[] result = Directories.Videos.listFiles();
        if (result == null) {
            throw new IOException("Unable to list content of directory " + Directories.Videos);
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
        log.info("Enqueuing existing resources for background video creation...");
        enqueueExistingResourceChecksums();
        log.info("Finished enqueuing existing resources for background video creation. Listening for new checksums.");
        while (!Thread.interrupted()) {
            try {
                Checksum checksum = backgroundCreationQueue.take();
                if (potentialVideoResource(checksum)) {
                    createIfMissing(checksum);
                }
            } catch (InterruptedException interrupted) {
                log.info("Videos background creation thread interrupted via exception", interrupted);
            } catch (Exception error) {
                log.error(error.getMessage(), error);
            }
        }
        log.info("Videos background creation thread stopping.");
    }

    private boolean potentialVideoResource(Checksum checksum) {
        List<ResourceDescription> resourcesForChecksum = core.getAssets().resourcesForChecksum.get(checksum);
        if (resourcesForChecksum == null || resourcesForChecksum.isEmpty()) {
            return true;
        }
        for (ResourceDescription resource : resourcesForChecksum) {
            if (ResourceClassification.movName(resource.name)) {
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
                    " existing resources for background video creation");
        } catch (Exception error) {
            log.error("Error while enqueuing existing resources for background video creation", error);
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
        File previousValue = videoForChecksum.put(checksum, file);
        if (previousValue != null) {
            throw new RuntimeException("Double occurence of " + checksum);
        }
    }

    synchronized public void createInBackgroundIfMissing(Checksum checksum) {
        if (checksum != null) {
            if (!backgroundCreationQueue.offer(checksum)) {
                log.error("Unable to enqueue checksum for background video creation: " + checksum);
            }
        }
    }

    synchronized private void createIfMissing(Checksum checksum) {
        File existingFile = videoForChecksum.get(checksum);
        if (existingFile == null || !existingFile.isFile()) {
            createVideo(checksum);
        }
    }

    private void createVideo(Checksum checksum) {
        try {
            File originalVideoFile = new File(Directories.Photos, checksum.toRawString());
            if (originalVideoFile.isFile()) {
                createAndWriteVideoContent(checksum, originalVideoFile);
            }
        } catch (Exception error) {
            log.error(error.getMessage(), error);
        }
    }

    private void createAndWriteVideoContent(Checksum checksum, File originalVideoFile) {
        // TODO
//        log.info("Not creating mp4 converted video for " + resourceType(checksum) + " resource " + checksum.toRawString() +
//                " - not yet implemented.");

        if (Windows) {
            return;
        }

        try {
            File videoFile = new File(Directories.Videos, checksum.toRawString() + ".mp4");
            // TODO check once calling ffmpeg via JNI
//            checkPath(videoFile);
            ProcessBuilder builder = new ProcessBuilder("./create_mp4.sh", checksum.toRawString());
            Process process = builder.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                videoForChecksum.put(checksum, videoFile);
            } else {
                log.error("Unable to create mp4 for " + resourceType(checksum) + " resource " + checksum.toRawString() +
                        " - exit code: " + exitCode);
                logSpecificErrorMessage(exitCode);
            }
        } catch (Exception error) {
            log.error("Unable to create mp4 for " + resourceType(checksum) + " resource " + checksum.toRawString(),
                    error);
        }
    }

    private void logSpecificErrorMessage(int exitCode) {
        String message = specificErrorMessage(exitCode);
        if (message != null) {
            log.error(message);
        }
    }

    private String specificErrorMessage(int exitCode) {
        switch (exitCode) {
            case 21:
                return "Output file already exists";
            case 22:
                return "Unable to delete existing temporary file";
            case 23:
                return "Not allowed to delete temporary file";
            case 24:
                return "Call to ffmpeg failed";
            case 25:
                return "Unable to rename temporary file";
            default:
                return null;
        }
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

    // TODO use for explicit path argument to command line; later for jni ffmpeg call
    private void checkPath(File videoFile) {
        if (!videoFile.toPath().startsWith(Directories.Videos.toPath())) {
            throw new RuntimeException("Not inside " + Directories.Videos.getAbsolutePath() +
                    ": " + videoFile.getAbsolutePath());
        }
        if (videoFile.toPath().startsWith(Directories.Photos.toPath())) {
            throw new RuntimeException("Inside " + Directories.Photos.getAbsolutePath() +
                    ": " + videoFile.getAbsolutePath());
        }
    }
}
