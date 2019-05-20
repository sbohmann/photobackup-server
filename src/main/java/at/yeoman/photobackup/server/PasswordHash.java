package at.yeoman.photobackup.server;

import at.yeoman.photobackup.server.primtive.ByteBlock;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class PasswordHash {
    private final ByteBlock salt;
    private final String password;
    private MessageDigest md;

    public ByteBlock result;

    public PasswordHash(ByteBlock salt, String password) throws Exception {
        this.salt = salt;
        this.password = password;
        md = MessageDigest.getInstance("SHA-512");

        calculateResult();
    }

    private void calculateResult() throws IOException {
        md.digest(salt.rawCopy());
        digestPassword();
    }

    private void digestPassword() throws IOException {
        ByteArrayOutputStream utf8EncodedPassword = new ByteArrayOutputStream();
        try (Writer writer = new OutputStreamWriter(utf8EncodedPassword, StandardCharsets.UTF_8)) {
            writer.write(password);
        }
        md.digest(utf8EncodedPassword.toByteArray());
        result = new ByteBlock(md.digest());
    }
}
