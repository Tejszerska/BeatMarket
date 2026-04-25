package com.spring.songify.infrastructure.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.spring.songify.infrastructure.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@RequiredArgsConstructor
@Component
class JwtGenerator {
    public static final String ROLES_CLAIM_NAME = "roles";

    private final AuthenticationManager authenticationManager;
    private final Clock clock = Clock.systemUTC();
    private final JwtConfigurationProperties properties;

    String authenticateAndGenerateToken(final String username, final String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        SecurityUser securityUser = (SecurityUser) authenticate.getPrincipal();

        Instant issuedAt = LocalDateTime.now(clock).toInstant(ZoneOffset.UTC);
        Instant expiresAt = issuedAt.plus(Duration.ofMinutes(properties.expirationMinutes()));

        Algorithm algorithm = Algorithm.HMAC256(properties.secret());

        return JWT.create()
                .withSubject(securityUser.getUsername())
                .withIssuedAt(issuedAt)
                .withExpiresAt(expiresAt)
                .withIssuer(properties.issuer())
                .withClaim(ROLES_CLAIM_NAME, securityUser.getAuthoritiesAsString())
                .sign(algorithm);
    }

}
