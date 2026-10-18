package com.spring.beatmarket.infrastructure.security.jwt;

import com.spring.beatmarket.infrastructure.security.jwt.dto.JwtGenerationDto;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CookieServiceTest {

    private final JwtTokenGenerator tokenGenerator = mock(JwtTokenGenerator.class);
    private final CookieService cookieService = new CookieService(tokenGenerator);

    @Test
    @DisplayName("Creates a secure access cookie after authenticating credentials")
    void creates_cookie_for_credentials() {
        // given
        JwtGenerationDto credentials = new JwtGenerationDto("member@example.com", "password");
        when(tokenGenerator.authenticateAndGenerateToken(credentials)).thenReturn("signed-token");

        // when
        Cookie cookie = cookieService.createAccessTokenCookie(credentials, 900);

        // then
        assertAccessCookie(cookie, "signed-token", 900);
        verify(tokenGenerator).authenticateAndGenerateToken(credentials);
    }

    @Test
    @DisplayName("Creates a secure access cookie for an OAuth user")
    void creates_cookie_for_oauth_user() {
        // given
        String email = "oauth@example.com";
        when(tokenGenerator.generateTokenForOAuthUser(email)).thenReturn("oauth-token");

        // when
        Cookie cookie = cookieService.createAccessTokenCookie(email, 360);

        // then
        assertAccessCookie(cookie, "oauth-token", 360);
        verify(tokenGenerator).generateTokenForOAuthUser(email);
    }

    @Test
    @DisplayName("Clears the access cookie")
    void clears_access_cookie() {
        // when
        Cookie cookie = cookieService.clearTokenCookie();

        // then
        assertAccessCookie(cookie, null, 0);
    }

    private void assertAccessCookie(Cookie cookie, String value, int maxAge) {
        assertThat(cookie.getName()).isEqualTo("accessToken");
        assertThat(cookie.getValue()).isEqualTo(value);
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.getSecure()).isTrue();
        assertThat(cookie.getPath()).isEqualTo("/");
        assertThat(cookie.getMaxAge()).isEqualTo(maxAge);
    }
}
