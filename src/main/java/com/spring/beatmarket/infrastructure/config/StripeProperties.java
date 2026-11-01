package com.spring.beatmarket.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "stripe")
public class StripeProperties {
    private String apiKey;
    private String webhookSecret;

}