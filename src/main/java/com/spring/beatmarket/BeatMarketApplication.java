package com.spring.beatmarket;

import com.spring.beatmarket.infrastructure.security.jwt.JwtConfigurationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(value = {JwtConfigurationProperties.class})
public class BeatMarketApplication {

    public static void main(String[] args) {
        SpringApplication.run(BeatMarketApplication.class, args);
    }

}
