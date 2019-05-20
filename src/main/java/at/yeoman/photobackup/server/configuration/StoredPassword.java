package at.yeoman.photobackup.server.configuration;

import at.yeoman.photobackup.server.Directories;
import at.yeoman.photobackup.server.PasswordHash;
import at.yeoman.photobackup.server.primtive.ByteBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

class StoredPassword {
    private static final File configurationFile = new File(Directories.Config, "password.txt");

    Logger log = LoggerFactory.getLogger(StoredPassword.class);

    final ByteBlock salt;
    final ByteBlock hash;

    StoredPassword(ByteBlock salt, ByteBlock hash) {
        this.salt = salt;
        this.hash = hash;
    }

    static StoredPassword load() {
        if (configurationFile.isFile()) {
            return new StoredPasswordLoader(configurationFile).loadFromFile();
        } else {
            return null;
        }
    }

    public boolean matches(String password) {
        try {
            return new PasswordHash(salt, password).result.equals(hash);
        } catch (Exception error) {
            log.error("Unable to calculate password hash", error);
            return false;
        }
    }
}
