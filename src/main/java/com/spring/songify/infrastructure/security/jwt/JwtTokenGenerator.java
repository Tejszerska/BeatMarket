package com.spring.songify.infrastructure.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.spring.songify.infrastructure.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
@RequiredArgsConstructor
public class JwtTokenGenerator {
    private final AuthenticationManager authenticationManager;
    private final Clock clock;
    private final JwtConfigurationProperties properties;
    private final KeyPair keyPair;
    private final UserDetailsService userDetailsService;

    public String authenticateAndGenerateToken(String username, String password) {
        UsernamePasswordAuthenticationToken authenticate = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = authenticationManager.authenticate(authenticate);
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        Instant issuedAt = LocalDateTime.now(clock).toInstant(ZoneOffset.UTC);
        Instant expiresAt = issuedAt.plus(Duration.ofMinutes(properties.expirationMinutes()));
        Algorithm algorithm = Algorithm.RSA256(null, (RSAPrivateKey) keyPair.getPrivate());
        return JWT.create()
                .withSubject(securityUser.getUsername())
                .withIssuedAt(issuedAt)
                .withExpiresAt(expiresAt)
                .withIssuer(properties.issuer())
                .withClaim("roles", securityUser.getAuthoritiesAsString())
                .sign(algorithm);
    }

    public String generateTokenForOAuthUser(String email) {
        SecurityUser securityUser = (SecurityUser) userDetailsService.loadUserByUsername(email);

        Instant issuedAt = LocalDateTime.now(clock).toInstant(ZoneOffset.UTC);
        Instant expiresAt = issuedAt.plus(Duration.ofMinutes(properties.expirationMinutes()));

        Algorithm algorithm = Algorithm.RSA256(null, (RSAPrivateKey) keyPair.getPrivate());

        return JWT.create()
                .withSubject(securityUser.getUsername())
                .withIssuedAt(issuedAt)
                .withExpiresAt(expiresAt)
                .withIssuer(properties.issuer())
                .withClaim("roles", securityUser.getAuthoritiesAsString())
                .sign(algorithm);
    }

}

