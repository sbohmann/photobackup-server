package at.yeoman.photobackup.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

class PasswordHash {
    private final byte[] salt;
    private final String password;
    private MessageDigest md;

    byte[] result;

    PasswordHash(byte[] salt, String password) throws Exception {
        this.salt = salt;
        this.password = password;
        md = MessageDigest.getInstance("PBKDF2");

        calculateResult();
    }

    private void calculateResult() throws IOException {
        md.digest(salt);
        digestPassword();
    }

    private void digestPassword() throws IOException {
        ByteArrayOutputStream utf8EncodedPassword = new ByteArrayOutputStream();
        new OutputStreamWriter(utf8EncodedPassword, StandardCharsets.UTF_8).write(password);
        result = md.digest(utf8EncodedPassword.toByteArray());
    }
}
