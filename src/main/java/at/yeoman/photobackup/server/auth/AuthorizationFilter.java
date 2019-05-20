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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuthorizationFilter implements Filter {
    private static Logger log = LoggerFactory.getLogger(AuthorizationFilter.class);

    private static final String AuthorizationKey = "Authorization";

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
            log.info("Redirecting [" + request.getServletPath() + "] to /login");
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
        return request.getServletPath().equals("/login") ||
                request.getServletPath().startsWith("/login/");
    }

    private boolean isAuthorized(HttpServletRequest request) {
        String token = authorizationFromRequest(request);
        return isValidToken(token);
    }

    private String authorizationFromRequest(HttpServletRequest request) {
        String headerValue = request.getHeader(AuthorizationKey);
        if (headerValue != null) {
            log.info("Attempting authorization from header");
            return headerValue;
        } else {
            return authorizationFromCookie(request);
        }
    }

    private String authorizationFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            return findAuthorizationCookie(cookies);
        }
        log.info("No authorization token from header nor cookie (no cookies found)");
        return null;
    }

    private String findAuthorizationCookie(Cookie[] cookies) {
        for (Cookie cookie : cookies) {
            if (isAuthorizationCookie(cookie)) {
                log.info("Attempting authorization from cookie value");
                return cookie.getValue();
            }
        }
        log.info("No authorization token from header nor cookie (no cookie of matching name found)");
        return null;
    }

    private boolean isAuthorizationCookie(Cookie cookie) {
        return cookie.getName().equals(AuthorizationKey);
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
