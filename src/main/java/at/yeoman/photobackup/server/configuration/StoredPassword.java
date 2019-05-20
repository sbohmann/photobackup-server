package at.yeoman.photobackup.server.configuration;

import at.yeoman.photobackup.server.Directories;

import java.io.File;

class StoredPassword {
    private static final File configurationFile = new File(Directories.Config, "password.txt");

    final byte[] selt;
    final byte[] hash;

    StoredPassword(byte[] selt, byte[] hash) {
        this.selt = selt;
        this.hash = hash;
    }

    static StoredPassword load() {
        if (configurationFile.isFile()) {
            return new StoredPasswordLoader(configurationFile).loadFromFile();
        } else {
            return null;
        }
    }
}
