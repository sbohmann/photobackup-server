package at.yeoman.photobackup.server.auth;

import at.yeoman.photobackup.server.configuration.SecurityConfiguration;
import at.yeoman.photobackup.server.primtive.ByteBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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

@Controller
public class LoginRequestHandler {
    private final Logger log = LoggerFactory.getLogger(LoginRequestHandler.class);

    private static final int ValiditySeconds = 100_000;

    private SecurityConfiguration configuration;
    private AuthorizationFilter filter;
    private SecureRandom random;

    @Autowired
    LoginRequestHandler(SecurityConfiguration configuration, AuthorizationFilter filter) {
        this.configuration = configuration;
        this.filter = filter;

        random = new SecureRandom();
    }

    @GetMapping(value = "/login")
    public String loginForm() {
        return "auth/login";
    }

    @PostMapping(value = "/login/form-data")
    public void loginFromForm(@RequestParam String password, HttpServletResponse response) throws IOException {
        login(password, response,
                token -> writeFormResponse(response, token),
                () -> writeFormLoginError(response));
    }

    private void writeFormResponse(HttpServletResponse response, Token token) {
        response.setStatus(HttpServletResponse.SC_FOUND);
        Cookie cookie = new Cookie("Authorization", token.id.toRawString());
        cookie.setMaxAge(-1);
        cookie.setPath("/");
        response.addCookie(cookie);
        response.setHeader("Location", "/gallery");
    }

    private void writeFormLoginError(HttpServletResponse response) {
//        response.setStatus(HttpServletResponse.SC_FOUND);
//        response.setHeader("Location", "/login");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("text/plain");
        getWriter(response).println("Login Error");
    }

    @PostMapping(value = "/login/api")
    public void loginFromApi(@RequestBody String password, HttpServletResponse response) throws IOException {
        login(password, response,
                token -> writeApiResponse(response, token),
                () -> writeApiLoginError(response));
    }

    private void writeApiResponse(HttpServletResponse response, Token token) {
        response.setContentType("text/plain");
        getWriter(response).println(token.id);
    }

    private void writeApiLoginError(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    private PrintWriter getWriter(HttpServletResponse response) {
        try {
            return response.getWriter();
        } catch (IOException error) {
            throw new RuntimeException(error);
        }
    }

    private void login(@RequestParam String password, HttpServletResponse response,
                       Consumer<Token> writeResponse, Runnable writeError) throws IOException {
        if (configuration.passwordMatches(password)) {
            log.info("Login successful");
            writeResponse.accept(createToken());
        } else {
            log.info("Login failed");
            writeError.run();
        }
    }

    private Token createToken() {
        Token result = new Token(new ByteBlock(64, random), Instant.now().plusSeconds(ValiditySeconds));
        filter.addToken(result);
        return result;
    }
}
