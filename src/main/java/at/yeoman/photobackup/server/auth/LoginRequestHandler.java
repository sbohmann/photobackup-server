package at.yeoman.photobackup.server.auth;

import at.yeoman.photobackup.server.configuration.SecurityConfiguration;
import at.yeoman.photobackup.server.primtive.ByteBlock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.function.Consumer;

@Component
public class LoginRequestHandler {
    private static final int ValiditySeconds = 100_000;;

    private SecurityConfiguration configuration;
    private AuthorizationFilter filter;
    private SecureRandom random;

    @Autowired
    LoginRequestHandler(SecurityConfiguration configuration, AuthorizationFilter filter) {
        this.configuration = configuration;
        this.filter = filter;

        random = new SecureRandom();
    }

    @PostMapping("/login/form-data")
    public void loginFromForm(@RequestParam String password, HttpServletResponse response) {
        login(password, response, token -> writeFormResponse(response, token));
    }

    private void writeFormResponse(HttpServletResponse response, Token token) {
        response.addCookie(new Cookie("Authorization", token.id.toRawString()));
        response.setHeader("Location", "/gallery");
        response.setStatus(HttpServletResponse.SC_FOUND);
    }

    @PostMapping("/login/api")
    public void loginFromApi(@RequestBody String password, HttpServletResponse response) {
        login(password, response, token -> writeApiResponse(response, token));
    }

    private void writeApiResponse(HttpServletResponse response, Token token) {
        response.setContentType("text/plain");
        getWriter(response).println(token.id);
    }

    private PrintWriter getWriter(HttpServletResponse response) {
        try {
            return response.getWriter();
        } catch (IOException error) {
            throw new RuntimeException(error);
        }
    }

    private void login(@RequestParam String password, HttpServletResponse response, Consumer<Token> writeResponse) {
        if (configuration.passwordMatches(password)) {
            writeResponse.accept(createToken());
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private Token createToken() {
        Token result = new Token(new ByteBlock(64, random), Instant.now().plusSeconds(ValiditySeconds));
        filter.addToken(result);
        return result;
    }
}