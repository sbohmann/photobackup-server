package at.yeoman.photobackup.server;

import at.yeoman.photobackup.server.primtive.ByteBlock;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

class PasswordCreation {
    private static final File passwordFile  = new File(Directories.Config, "password.txt");

    private String newPassword;
    private ByteBlock salt;
    private ByteBlock hash;

    void run() throws Exception {
        readNewPasswordFromPrompt();
        createSaltAndHash();
        writePasswordFile();
    }

    private void createSaltAndHash() throws Exception {
        createSalt();
        createHash();
    }

    private void readNewPasswordFromPrompt() throws IOException {
        BufferedReader in = createStdinReader();
        System.out.println("Enter new password:");
        String password = in.readLine();
        System.out.println("Repeat new password:");
        String repeatedPassword = in.readLine();

        if (password.equals(repeatedPassword)) {
            if (password.length() == 0) {
                throw new IllegalArgumentException("Empty password");
            }

            newPassword = password;
        } else {
            throw new IllegalArgumentException("Password mismatch");
        }
    }

    private BufferedReader createStdinReader() {
        return new BufferedReader(new InputStreamReader(System.in));
    }

    private void createSalt() {
        SecureRandom random = new SecureRandom();
        salt = new ByteBlock(64, random);
    }

    private void createHash() throws Exception {
        hash = new PasswordHash(salt, newPassword).result;
    }

    private void writePasswordFile() throws IOException {
        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(passwordFile), StandardCharsets.UTF_8))) {
            out.println(salt.toRawString());
            out.println(hash.toRawString());
        }
    }
}
