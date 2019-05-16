package at.yeoman.photobackup.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.nio.channels.FileLock;
import java.util.Properties;

@SpringBootApplication
public class Server {
    private static final Logger log = LoggerFactory.getLogger(Server.class);
    
    public static void main(String[] args) throws IOException {
        FileLock lock = new RandomAccessFile("photobackup-server.lock", "rw").getChannel().tryLock();
        if (lock == null) {
            throw new IllegalStateException("Unable to aquire file lock");
        }
        for (File directory : Directories.values) {
            log.info("Found directory [" + directory + "]");
        }

        SpringApplication application = new SpringApplication(Server.class);
        configureApplication(application);
        application.run(args);
    }

    private static void configureApplication(SpringApplication application) throws IOException {
        File applicationPropertiesFile = new File("config/spring.properties");
        if (applicationPropertiesFile.isFile()) {
            Properties properties = new Properties();
            properties.load(new FileInputStream(applicationPropertiesFile));
            application.setDefaultProperties(properties);
        }
    }
}
