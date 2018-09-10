package at.yeoman.photobackup.server;

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
