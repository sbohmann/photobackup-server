package at.yeoman.photobackup.server.core;

import at.yeoman.photobackup.server.api.AssetDescription;
import at.yeoman.photobackup.server.configuration.CoreConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Core {
    private CoreConfiguration configuration;
    private final Assets assets;

    @Autowired
    Core(CoreConfiguration configuration) {
        this.configuration = configuration;
        assets = new AssetsFromFileSystem(configuration).result;
    }

    public void getAssets() {

    }
}
