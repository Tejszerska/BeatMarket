package com.spring.beatmarket.infrastructure.security.jwt;

import jakarta.servlet.http.Cookie;
import org.springframework.stereotype.Component;

@Component
public class CookieService {

    public Cookie createAccessTokenCookie(String token, int expirationInSeconds) {
        Cookie cookie = new Cookie("accessToken", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(expirationInSeconds);
        return cookie;
    }
}