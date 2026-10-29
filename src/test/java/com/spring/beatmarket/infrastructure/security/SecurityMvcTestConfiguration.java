package com.spring.beatmarket.infrastructure.security;

import com.spring.beatmarket.infrastructure.security.jwt.JwtAuthTokenFilter;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

@TestConfiguration
@Import(SecurityConfig.class)
public class SecurityMvcTestConfiguration {

    @Bean
    KeyPair jwtTestKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    @Bean
    JwtAuthTokenFilter jwtAuthTokenFilter(KeyPair jwtTestKeyPair) {
        return new JwtAuthTokenFilter(jwtTestKeyPair);
    }
}
