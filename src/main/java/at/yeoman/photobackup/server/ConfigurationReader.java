package at.yeoman.photobackup.server;

import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
class ConfigurationReader {
    private Configuration result;

    ConfigurationReader() {
        result = new Configuration();
        result.setDescription("original configured production description");
    }

    @Bean
    Configuration getConfiguration() {
        return result;
    }
}
