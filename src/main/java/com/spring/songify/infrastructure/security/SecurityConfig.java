package com.spring.songify.infrastructure.security;

import com.spring.songify.domain.usercrud.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
class SecurityConfig {
    @Bean
    public UserDetailsManager userDetailsService(UserRepository userRepository) {
        return new UserDetailsServiceImpl(userRepository, passwordEncoder());
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(c -> c.disable());
        http.formLogin(Customizer.withDefaults());
        http.httpBasic(Customizer.withDefaults());
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/swagger-resources/**").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/users/register/**").permitAll()
                // GENRES endpoint rules
                .requestMatchers(HttpMethod.GET, "/genres/**").permitAll()
                .requestMatchers(HttpMethod.PATCH, "/genres/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/genres/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/genres/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/genres/**").hasRole("ADMIN")
                // SONGS endpoint rules
                .requestMatchers(HttpMethod.GET, "/songs/**").permitAll()
                .requestMatchers(HttpMethod.PATCH, "/songs/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/songs/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/songs/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/songs/**").hasRole("ADMIN")
                // ALBUMS endpoint rules
                .requestMatchers(HttpMethod.GET, "/albums/**").permitAll()
                .requestMatchers(HttpMethod.PATCH, "/albums/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/albums/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/albums/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/albums/**").hasRole("ADMIN")
                // ARTISTS endpoint rules
                .requestMatchers(HttpMethod.GET, "/artists/**").permitAll()
                .requestMatchers(HttpMethod.PATCH, "/artists/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/artists/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/artists/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/artists/**").hasRole("ADMIN")
                .anyRequest().authenticated());
        return http.build();
    }
}
