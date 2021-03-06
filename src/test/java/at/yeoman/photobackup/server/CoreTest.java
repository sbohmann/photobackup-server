package at.yeoman.photobackup.server;

import at.yeoman.photobackup.server.configuration.CoreConfiguration;
import at.yeoman.photobackup.server.core.Core;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest
public class CoreTest {
    @Autowired
    private Core core;

    @Test
    public void nothing() {
        System.out.println("This is fine.");
    }

    @TestConfiguration
    static class Config {
        @Bean
        CoreConfiguration getConfiguration() {
            return new CoreConfiguration();
        }
    }
}
