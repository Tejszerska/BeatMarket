package com.spring.beatmarket.infrastructure.security;

import com.spring.beatmarket.domain.account.UserFacade;
import com.spring.beatmarket.infrastructure.security.jwt.CookieService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@AllArgsConstructor
@Component
class CustomSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private final CookieService cookieService;
    private final UserFacade userFacade;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        String email = oidcUser.getEmail();

        userFacade.registerNewUser(email);

        Cookie cookie = cookieService.createAccessTokenCookie(email, 360);
        response.addCookie(cookie);

        this.setAlwaysUseDefaultTargetUrl(true);
        this.setDefaultTargetUrl("https://localhost:8443/swagger-ui/index.html");
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
