package com.spring.beatmarket.infrastructure.security;

import com.spring.beatmarket.infrastructure.security.jwt.JwtAuthTokenFilter;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

@TestConfiguration
@Import(SecurityConfig.class)
public class SecurityMvcTestConfiguration {

    @Bean
    JwtAuthTokenFilter jwtAuthTokenFilter() throws NoSuchAlgorithmException {
        return new JwtAuthTokenFilter(KeyPairGenerator.getInstance("RSA").generateKeyPair());
    }
}
