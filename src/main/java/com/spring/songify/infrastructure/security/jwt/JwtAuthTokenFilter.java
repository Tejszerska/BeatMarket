package com.spring.songify.infrastructure.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthTokenFilter extends OncePerRequestFilter {
    private final KeyPair keyPair;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // getting token from custom JWT
        String token = null;

        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
            if (token == null) {
                String authorization = request.getHeader("Authorization");
                if (authorization != null && authorization.startsWith("Bearer ")) {
                    token = authorization.substring(7);
                }
            }

            if (token == null) {
                filterChain.doFilter(request, response);
                return;
            }

            try {
                JWTVerifier jwtVerifier = JWT.require(Algorithm.RSA256((RSAPublicKey) keyPair.getPublic(), null)).build();
                DecodedJWT decodedToken = jwtVerifier.verify(token);

                String login = decodedToken.getSubject();
                List<SimpleGrantedAuthority> roles = decodedToken.getClaim("roles")
                        .asList(String.class)
                        .stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                        = new UsernamePasswordAuthenticationToken(login, null, roles);
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            } catch (Exception e) {
                log.error("Token verification failed: {}", e.getMessage());
            }

            filterChain.doFilter(request, response);
        }
    }
