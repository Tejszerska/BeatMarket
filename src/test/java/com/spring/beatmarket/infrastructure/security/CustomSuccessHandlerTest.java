package com.spring.beatmarket.infrastructure.security;

import com.spring.beatmarket.domain.account.UserFacade;
import com.spring.beatmarket.infrastructure.security.jwt.CookieService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomSuccessHandlerTest {

    private final CookieService cookieService = mock(CookieService.class);
    private final UserFacade userFacade = mock(UserFacade.class);
    private final CustomSuccessHandler successHandler = new CustomSuccessHandler(cookieService, userFacade);

    @Test
    @DisplayName("Registers the OAuth user, adds an access cookie, and redirects")
    void registers_oauth_user_and_redirects() throws ServletException, IOException {
        // given
        String email = "oauth@example.com";
        OidcUser oidcUser = mock(OidcUser.class);
        when(oidcUser.getEmail()).thenReturn(email);
        UsernamePasswordAuthenticationToken authentication =
                UsernamePasswordAuthenticationToken.authenticated(oidcUser, null, List.of());
        Cookie accessCookie = new Cookie("accessToken", "signed-token");
        when(cookieService.createAccessTokenCookie(email, 360)).thenReturn(accessCookie);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // then
        verify(userFacade).registerNewUser(email);
        verify(cookieService).createAccessTokenCookie(email, 360);
        assertThat(response.getCookies()).contains(accessCookie);
        assertThat(response.getStatus()).isEqualTo(302);
        assertThat(response.getRedirectedUrl()).isEqualTo("https://localhost:8443/swagger-ui/index.html");
    }
}
