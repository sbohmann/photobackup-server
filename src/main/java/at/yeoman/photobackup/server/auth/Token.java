package at.yeoman.photobackup.server.auth;

import at.yeoman.photobackup.server.primtive.ByteBlock;

import java.time.Instant;

public class Token {
    final ByteBlock id;
    final Instant endOfValidity;

    public Token(ByteBlock id, Instant endOfValidity) {
        this.id = id;
        this.endOfValidity = endOfValidity;
    }

    public boolean isValid() {
        return Instant.now().compareTo(endOfValidity) < 0;
    }
}
