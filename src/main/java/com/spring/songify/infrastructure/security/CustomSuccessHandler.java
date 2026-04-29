package com.spring.songify.infrastructure.security;

import com.spring.songify.domain.usercrud.User;
import com.spring.songify.domain.usercrud.UserRepository;
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

        setResponseCookie(response, oidcUser.getIdToken().getTokenValue());
        this.setAlwaysUseDefaultTargetUrl(true);
        this.setDefaultTargetUrl("/identity/oauth");
        super.onAuthenticationSuccess(request, response, authentication);
    }

    private void setResponseCookie(HttpServletResponse response, String tokenValue) {
        Cookie cookie = new Cookie("accessToken", tokenValue);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60); // 1 hour
        response.addCookie(cookie);
    }
}
