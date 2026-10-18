package com.spring.beatmarket.infrastructure.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.spring.beatmarket.domain.account.User;
import com.spring.beatmarket.infrastructure.security.SecurityUser;
import com.spring.beatmarket.infrastructure.security.jwt.dto.JwtGenerationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtTokenGeneratorTest {

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    UserDetailsService userDetailsService;

    private JwtTokenGenerator jwtTokenGenerator;
    private KeyPair testKeyPair;

    @BeforeEach
    void setUp() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        testKeyPair = keyPairGenerator.generateKeyPair();

        Clock fixedClock = Clock.fixed(Instant.parse("2024-01-01T12:00:00Z"), ZoneId.of("UTC"));
        JwtConfigurationProperties properties = new JwtConfigurationProperties(10, "BeatMarketApp");

        jwtTokenGenerator = new JwtTokenGenerator(
                authenticationManager,
                fixedClock,
                properties,
                testKeyPair,
                userDetailsService
        );
    }

    @Test
    @DisplayName("Generates a valid JWT for an OAuth user")
    void generates_valid_token_for_oauth_user() {
        // given
        String email = "test@gmail.com";
        SecurityUser securityUser = createSecurityUser(email);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(securityUser);

        // when
        String token = jwtTokenGenerator.generateTokenForOAuthUser(email);

        // then
        assertTokenClaims(token, email);
        verify(userDetailsService).loadUserByUsername(email);
    }

    @Test
    @DisplayName("Generates a valid JWT after authenticating credentials")
    void generates_valid_token_after_authentication() {
        // given
        JwtGenerationDto credentials = new JwtGenerationDto("member@example.com", "password");
        SecurityUser securityUser = createSecurityUser(credentials.email());
        Authentication authenticated = UsernamePasswordAuthenticationToken.authenticated(
                securityUser,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authenticated);

        // when
        String token = jwtTokenGenerator.authenticateAndGenerateToken(credentials);

        // then
        assertTokenClaims(token, credentials.email());
        ArgumentCaptor<UsernamePasswordAuthenticationToken> authenticationCaptor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(authenticationCaptor.capture());
        assertThat(authenticationCaptor.getValue().getPrincipal()).isEqualTo(credentials.email());
        assertThat(authenticationCaptor.getValue().getCredentials()).isEqualTo(credentials.password());
    }

    @Test
    @DisplayName("Does not generate a JWT when credential authentication fails")
    void rejects_invalid_credentials() {
        // given
        JwtGenerationDto credentials = new JwtGenerationDto("member@example.com", "wrong-password");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // when & then
        assertThatThrownBy(() -> jwtTokenGenerator.authenticateAndGenerateToken(credentials))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid credentials");
    }

    private SecurityUser createSecurityUser(String email) {
        User user = User.builder()
                .email(email)
                .password("encoded-password")
                .authorities(List.of("ROLE_USER"))
                .enabled(true)
                .build();
        return new SecurityUser(user);
    }

    private void assertTokenClaims(String token, String email) {
        DecodedJWT decodedJWT = JWT.decode(token);
        Algorithm.RSA256((RSAPublicKey) testKeyPair.getPublic(), null).verify(decodedJWT);

        assertThat(decodedJWT.getSubject()).isEqualTo(email);
        assertThat(decodedJWT.getIssuer()).isEqualTo("BeatMarketApp");
        assertThat(decodedJWT.getClaim("roles").asList(String.class)).containsExactly("ROLE_USER");
        assertThat(decodedJWT.getIssuedAtAsInstant()).isEqualTo(Instant.parse("2024-01-01T12:00:00Z"));
        assertThat(decodedJWT.getExpiresAtAsInstant()).isEqualTo(Instant.parse("2024-01-01T12:10:00Z"));
    }
}
