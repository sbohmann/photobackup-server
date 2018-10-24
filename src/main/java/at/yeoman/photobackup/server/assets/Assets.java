package at.yeoman.photobackup.server.assets;

import java.util.ArrayList;
import java.util.List;

class Assets {
    private List<Asset> assets;

    Assets() {
        assets= new ArrayList<>();
    }

    Assets(List<Asset> assets) {
        this.assets = assets;
    }
}
