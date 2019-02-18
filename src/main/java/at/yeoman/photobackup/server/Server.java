package at.yeoman.photobackup.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;

@SpringBootApplication
public class Server {
    public static void main(String[] args) throws IOException {
        FileLock lock = new RandomAccessFile("photobackup-server.lock", "rw").getChannel().tryLock();
        if (lock == null) {
            throw new IllegalStateException("Unable to aquire file lock");
        }
        SpringApplication.run(Server.class, args);
    }
}
