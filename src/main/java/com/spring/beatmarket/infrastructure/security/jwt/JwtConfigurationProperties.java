package com.spring.beatmarket.infrastructure.security.jwt;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(value = "auth.jwt")
public record JwtConfigurationProperties(
        long expirationMinutes,
        String issuer
) {
}