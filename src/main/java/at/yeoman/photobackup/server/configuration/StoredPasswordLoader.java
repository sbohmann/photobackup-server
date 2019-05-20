package at.yeoman.photobackup.server.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.codec.Hex;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

class StoredPasswordLoader {
    private static final Logger log = LoggerFactory.getLogger(StoredPasswordLoader.class);

    private File file;

    StoredPasswordLoader(File file) {
        this.file = file;
    }

    StoredPassword loadFromFile() {
        try {
            return loadFromFileOrThrow();
        } catch (Exception error) {
            log.error("Unable to load password from file [" + file + "]", error);
            throw new RuntimeException(error);
        }
    }

    private StoredPassword loadFromFileOrThrow() throws IOException {
        BufferedReader in = createReader();
        List<String> lines = readTwoLines(in);
        byte[] salt = Hex.decode(lines.get(0));
        byte[] hash = Hex.decode(lines.get(1));
        return new StoredPassword(salt, hash);
    }

    private BufferedReader createReader() throws FileNotFoundException {
        return new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
    }

    private List<String> readTwoLines(BufferedReader in) throws IOException {
        ArrayList<String> result = new ArrayList<>();
        result.add(readNonNullLine(in));
        result.add(readNonNullLine(in));
        detectEndOfFile(in);
        return result;
    }

    private String readNonNullLine(BufferedReader in) throws IOException {
        String result = in.readLine();
        if (result == null) {
            throw new IOException("Unexpected end of file");
        }
        return result;
    }

    private void detectEndOfFile(BufferedReader in) throws IOException {
        String line = in.readLine();
        if (line == null) {
            return;
        } else if (line.equals("")) {
            String nextLine = in.readLine();
            if (nextLine == null) {
                return;
            }
        }
        throw new IOException("Unexpected text at end of file");
    }
}
