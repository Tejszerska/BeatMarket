package com.spring.songify.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
class SecurityConfig {
    @Bean
    public UserDetailsService userDetailsService(){
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        UserDetails user1 = User.withUsername("basia")
                .password("123")
                .roles("USER")
                .build();
        UserDetails user2 = User.withUsername("admin")
                .password("123")
                .roles("USER", "ADMIN")
                .build();

        manager.createUser(user1);
        manager.createUser(user2);
        return manager;
    }

    @Bean
    PasswordEncoder passwordEncoder (){
        return NoOpPasswordEncoder.getInstance();
    }
}
