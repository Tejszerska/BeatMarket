package com.spring.beatmarket.infrastructure.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class JwtAuthTokenFilterTest {

    private KeyPair keyPair;
    private JwtAuthTokenFilter filter;

    @BeforeEach
    void setUp() throws NoSuchAlgorithmException {
        SecurityContextHolder.clearContext();
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        keyPair = keyPairGenerator.generateKeyPair();
        filter = new JwtAuthTokenFilter(keyPair);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Continues the filter chain when no token is provided")
    void continues_without_token() throws ServletException, IOException {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        // when
        filter.doFilter(request, response, chain);

        // then
        verify(chain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Authenticates the subject and roles from the access cookie")
    void authenticates_access_cookie() throws ServletException, IOException {
        // given
        String token = createToken("cookie@example.com", List.of("ROLE_CUSTOMER"));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("accessToken", token));
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        // when
        filter.doFilter(request, response, chain);

        // then
        assertAuthentication("cookie@example.com", "ROLE_CUSTOMER");
        verify(chain).doFilter(request, response);
    }

    @Test
    @DisplayName("Authenticates a bearer token when the access cookie is absent")
    void authenticates_bearer_token() throws ServletException, IOException {
        // given
        String token = createToken("bearer@example.com", List.of("ROLE_CUSTOMER", "ROLE_ADMIN"));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("other", "ignored"));
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        // when
        filter.doFilter(request, response, chain);

        // then
        assertAuthentication("bearer@example.com", "ROLE_CUSTOMER", "ROLE_ADMIN");
        verify(chain).doFilter(request, response);
    }

    @Test
    @DisplayName("Prefers the access cookie over the bearer token")
    void prefers_access_cookie() throws ServletException, IOException {
        // given
        String cookieToken = createToken("cookie@example.com", List.of("ROLE_COOKIE"));
        String bearerToken = createToken("bearer@example.com", List.of("ROLE_BEARER"));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("accessToken", cookieToken));
        request.addHeader("Authorization", "Bearer " + bearerToken);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        // when
        filter.doFilter(request, response, chain);

        // then
        assertAuthentication("cookie@example.com", "ROLE_COOKIE");
        verify(chain).doFilter(request, response);
    }

    @Test
    @DisplayName("Continues without authentication when the token is invalid")
    void continues_with_invalid_token() throws ServletException, IOException {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        // when
        filter.doFilter(request, response, chain);

        // then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(chain).doFilter(request, response);
    }

    private String createToken(String subject, List<String> roles) {
        return JWT.create()
                .withSubject(subject)
                .withClaim("roles", roles)
                .sign(Algorithm.RSA256(null, (RSAPrivateKey) keyPair.getPrivate()));
    }

    private void assertAuthentication(String subject, String... roles) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        assertThat(authentication).isNotNull();
        assertThat(authentication.getPrincipal()).isEqualTo(subject);
        assertThat(authentication.getAuthorities())
                .extracting("authority")
                .containsExactlyInAnyOrder(roles);
        assertThat(authentication.isAuthenticated()).isTrue();
    }
}
