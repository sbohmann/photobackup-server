package at.yeoman.photobackup.server.auth;

import at.yeoman.photobackup.server.configuration.SecurityConfiguration;
import at.yeoman.photobackup.server.primtive.ByteBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuthorizationFilter implements Filter {
    private static Logger log = LoggerFactory.getLogger(AuthorizationFilter.class);

    private SecurityConfiguration configuration;
    private ConcurrentHashMap<ByteBlock, Token> tokens;

    @Autowired
    AuthorizationFilter(SecurityConfiguration configuration) {
        this.configuration = configuration;
        tokens = new ConcurrentHashMap<>();
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest rawRequest, ServletResponse rawResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = cast(rawRequest);
        HttpServletResponse response = cast(rawResponse);

        filterRequest(chain, request, response);
    }

    private HttpServletRequest cast(ServletRequest request) {
        return (HttpServletRequest) request;
    }

    private HttpServletResponse cast(ServletResponse response) {
        return (HttpServletResponse) response;
    }

    private void filterRequest(FilterChain chain, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (permitted(request)) {
            chain.doFilter(request, response);
        } else {
            log.debug("Redirecting to /login");
            response.setHeader("Location", "/login");
            response.setStatus(HttpServletResponse.SC_FOUND);
        }
    }

    private boolean permitted(HttpServletRequest request) {
        return isLoginRequest(request) ||
                !configuration.isAuthorizationEnabled() ||
                isAuthorized(request);
    }

    private boolean isLoginRequest(HttpServletRequest request) {
        return getPath(request).startsWith("/login/");
    }

    private String getPath(HttpServletRequest request) {
        return cast(request).getServletPath();
    }

    private boolean isAuthorized(HttpServletRequest request) {
        return isValidToken(authorizationFromRequest(request));
    }

    private String authorizationFromRequest(HttpServletRequest request) {
        String headerValue = request.getHeader("Authorization");
        if (headerValue != null) {
            return headerValue;
        } else {
            return getAuthorizationCookie(request);
        }
    }

    private String getAuthorizationCookie(HttpServletRequest request) {
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals("Authorization")) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private boolean isValidToken(String key) {
        if (key == null) {
            return false;
        }
        if (!ByteBlock.HexStringPattern.matcher(key).matches()) {
            return false;
        }
        return validTokenStoredForId(parse(key));
    }

    private ByteBlock parse(String key) {
        return new ByteBlock(key);
    }

    private boolean validTokenStoredForId(ByteBlock id) {
        Token token = tokens.get(id);
        return token != null && token.isValid();
    }

    void addToken(Token token) {
        Token previousToken = tokens.putIfAbsent(token.id, token);
        if (previousToken != null) {
            throw new IllegalArgumentException("Token ID collision [" + token.id + "]");
        }
    }
}
