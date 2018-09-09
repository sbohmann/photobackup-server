package at.yeoman.photobackup.server;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class CoreTest {
    @Autowired
    Configuration configuration;

    @Autowired
    Core core;

    @Test
    public void coreIsGood() {
        //Mockito.when(configuration.getDescription()).thenReturn("faux description");
        System.out.println(core.createText("faux name"));
    }

    @TestConfiguration
    static class Config {
        @Bean
        Configuration getConfiguration() {
            Configuration result = new Configuration();
            result.setDescription("faux description");
            return result;
        }
    }
}
