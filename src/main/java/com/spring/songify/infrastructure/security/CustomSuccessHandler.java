package com.spring.songify.infrastructure.security;

import com.spring.songify.domain.usercrud.User;
import com.spring.songify.domain.usercrud.UserRepository;
import com.spring.songify.infrastructure.security.jwt.CookieService;
import com.spring.songify.infrastructure.security.jwt.JwtTokenGenerator;
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
import java.util.List;

@AllArgsConstructor
@Component
class CustomSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private final static List<String> DEFAULT_USER_ROLES = List.of("ROLE_ADMIN", "ROLE_USER");
    private final UserRepository userRepository;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final CookieService cookieService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        String email = oidcUser.getEmail();
        // Auto register
        if (!userRepository.existsByEmail(email)) {
            User newUser = User.builder()
                    .email(email)
                    .password(null)
                    .authorities(DEFAULT_USER_ROLES)
                    .enabled(true)
                    .build();
            userRepository.save(newUser);
        }
        String customToken = jwtTokenGenerator.generateTokenForOAuthUser(email);

        Cookie cookie = cookieService.createAccessTokenCookie(customToken, 360);
        response.addCookie(cookie);

        this.setAlwaysUseDefaultTargetUrl(true);
        this.setDefaultTargetUrl("https://localhost:8443/swagger-ui/index.html");
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
