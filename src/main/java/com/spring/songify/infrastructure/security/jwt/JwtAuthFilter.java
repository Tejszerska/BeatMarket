package com.spring.songify.infrastructure.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

@RequiredArgsConstructor
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final KeyPair keyPair;

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        if (authorization == null) {
            filterChain.doFilter(request, response);
            return;
        }

        JWTVerifier jwtVerifier = JWT
                .require(Algorithm.RSA256((RSAPublicKey) keyPair.getPublic(), null))
                .build();

        String token = authorization.substring(7);
        DecodedJWT decodedJWT = jwtVerifier.verify(token);

        String login = decodedJWT.getSubject();

        List<SimpleGrantedAuthority> roles = decodedJWT.getClaim("roles")
                .asList(String.class)
                .stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(login, null, roles);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }
}
