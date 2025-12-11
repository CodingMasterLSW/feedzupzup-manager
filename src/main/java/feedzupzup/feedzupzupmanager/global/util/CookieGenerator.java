package feedzupzup.feedzupzupmanager.global.util;

import jakarta.servlet.http.Cookie;
import org.springframework.stereotype.Component;

@Component
public class CookieGenerator {

    private static final String SESSION_COOKIE_NAME = "JSESSIONID";
    private static final String COOKIE_PATH = "/";

    public Cookie createExpiredSessionCookie() {
        final Cookie cookie = new Cookie(SESSION_COOKIE_NAME, null);
        cookie.setPath(COOKIE_PATH);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        return cookie;
    }
}
