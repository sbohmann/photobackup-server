package at.yeoman.photobackup.server.assets;

import java.time.Instant;
import java.util.List;

class Asset {
    private String name;
    private Instant creationTime;
    private List<Resource> resources;
}
