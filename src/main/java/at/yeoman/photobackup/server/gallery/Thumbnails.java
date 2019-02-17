package at.yeoman.photobackup.server.gallery;

import at.yeoman.photobackup.server.Directories;
import at.yeoman.photobackup.server.assets.Checksum;
import at.yeoman.photobackup.server.imageMagick.ImageMagick;
import at.yeoman.photobackup.server.io.FileContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class Thumbnails {
    Logger log = LoggerFactory.getLogger(Thumbnails.class);
    private static final Pattern thumbnailFileNamePattern = Pattern.compile("([0-9a-zA-Z]{128}).jpg");
    private Map<Checksum, File> thumbnailForChecksum= new HashMap<>();

    Thumbnails() throws IOException {
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
            log.info("Thumbnail creation failed (null) for resource " + checksum.toRawString());
            return null;
        }
        if (thumbnailContent.length == 0) {
            log.info("Thumbnail creation failed (empty data) for resource " + checksum.toRawString());
            return null;
        }
        writeThumbnailFile(checksum, thumbnailContent);
        return thumbnailContent;
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
