package at.yeoman.photobackup.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ConfigurationReader {
    private CoreConfiguration result;

    ConfigurationReader() {
        result = new CoreConfiguration();
    }

    @Bean
    CoreConfiguration getConfiguration() {
        return result;
    }
}
