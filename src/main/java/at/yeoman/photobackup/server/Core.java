package at.yeoman.photobackup.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Core {
    private Configuration configuration;

    @Autowired
    Core(Configuration configuration) {
        this.configuration = configuration;
    }

    String createText(String name) {
        return "Core says [" + configuration.getDescription() + "] is the configured description! --> (((" + name + ")))";
    }
}
