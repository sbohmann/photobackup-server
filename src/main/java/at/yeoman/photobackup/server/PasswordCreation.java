package at.yeoman.photobackup.server;

import org.apache.commons.codec.binary.Hex;

import java.io.*;
import java.security.SecureRandom;

class PasswordCreation {
    private static final File passwordFile  = new File(Directories.Config, "password.txt");

    private String newPassword;
    private byte[] salt;
    private byte[] hash;

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
            newPassword = password;
        }
    }

    private BufferedReader createStdinReader() {
        return new BufferedReader(new InputStreamReader(System.in));
    }

    private void createSalt() {
        SecureRandom random = new SecureRandom();
        salt = new byte[256];
        random.nextBytes(salt);
    }

    private void createHash() throws Exception {
        hash = new PasswordHash(salt, newPassword).result;
    }

    private void writePasswordFile() throws IOException {
        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(passwordFile)))) {
            out.println(Hex.encodeHex(salt));
            out.println(Hex.encodeHex(hash));
        }
    }
}
