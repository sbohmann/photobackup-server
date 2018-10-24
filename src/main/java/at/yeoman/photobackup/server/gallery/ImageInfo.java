package at.yeoman.photobackup.server.gallery;

import java.time.LocalDate;

class ImageInfo {
    private final LocalDate creationDate;
    private final ImageType imageType;

    public ImageInfo(LocalDate creationDate, ImageType imageType) {
        this.creationDate = creationDate;
        this.imageType = imageType;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public ImageType getImageType() {
        return imageType;
    }
}
