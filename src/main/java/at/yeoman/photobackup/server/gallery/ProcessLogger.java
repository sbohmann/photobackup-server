package at.yeoman.photobackup.server.gallery;

import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

class ProcessLogger {
    private final Logger log;
    
    ProcessLogger(Process process, Logger log) {
        this.log = log;
        createStreamHandlingThread(process.getInputStream(), "stdout", log::info);
        createStreamHandlingThread(process.getErrorStream(), "stderr", log::error);
    }
    
    private void createStreamHandlingThread(InputStream stream, String name, Consumer<String> logLine) {
        new Thread(() -> this.processStream(stream, name, logLine))
                .run();
    }
    
    private void processStream(InputStream stream, String name, Consumer<String> logLine) {
        BufferedReader in = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        try {
            while (true) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }
                log.info(name + ": " + line);
            }
        } catch (IOException error) {
            log.error("Error reading from process steam [" + name + "]", error);
        }
    }
}
