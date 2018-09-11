package at.yeoman.photobackup.server.core;

import at.yeoman.photobackup.server.configuration.CoreConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Core {
    private CoreConfiguration configuration;

    @Autowired
    Core(CoreConfiguration configuration) {
        this.configuration = configuration;
    }

    public String getName() {
        return "";
    }
}
