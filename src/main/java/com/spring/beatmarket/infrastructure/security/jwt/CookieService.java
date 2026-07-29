package com.spring.beatmarket.infrastructure.security.jwt;

import com.spring.beatmarket.infrastructure.security.jwt.dto.JwtGenerationDto;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CookieService {
    private final JwtTokenGenerator tokenGenerator;

    public Cookie createAccessTokenCookie(JwtGenerationDto jwtDto, int expirationInSeconds) {
        String token = tokenGenerator.authenticateAndGenerateToken(jwtDto);

        Cookie cookie = new Cookie("accessToken", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(expirationInSeconds);
        return cookie;
    }

    public Cookie createAccessTokenCookie(String email, int expirationInSeconds) {
        String token = tokenGenerator.generateTokenForOAuthUser(email);
        Cookie cookie = new Cookie("accessToken", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(expirationInSeconds);
        return cookie;
    }

    public Cookie clearTokenCookie(){
        Cookie cookie = new Cookie("accessToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        return cookie;
    }
}