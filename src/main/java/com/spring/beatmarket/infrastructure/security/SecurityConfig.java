package com.spring.beatmarket.infrastructure.security;

import com.spring.beatmarket.domain.account.UserRepository;
import com.spring.beatmarket.infrastructure.security.jwt.JwtAuthTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
class SecurityConfig {
    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return new UserDetailsServiceImpl(userRepository);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationSuccessHandler successHandler, JwtAuthTokenFilter jwtAuthTokenFilter) throws Exception {
        http.csrf(c -> c.disable());
        http.formLogin(c -> c.disable());
        http.httpBasic(c -> c.disable());

        http.oauth2Login(oauth2 -> oauth2
                .successHandler(successHandler)
        );
        http.exceptionHandling(c -> c.authenticationEntryPoint(
                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
        ));
        http.sessionManagement( c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(jwtAuthTokenFilter, UsernamePasswordAuthenticationFilter.class);

        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/**").permitAll()

                // SWAGGER & API DOCS
                .requestMatchers(
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/v3/api-docs",
                        "/swagger-resources/**",
                        "/webjars/**",
                        "/error"
                ).permitAll()
//                // LOGIN & REGISTER
//                .requestMatchers("/api/users/register/**").permitAll()
//                .requestMatchers("/api/users/confirm/**").permitAll()
//                .requestMatchers("/api/users/login/**").permitAll()
//                .requestMatchers(HttpMethod.POST, "/api/identity/**").permitAll()
//                .requestMatchers(HttpMethod.GET, "/api/identity/email").authenticated()
//                // GENRES endpoint rules
//                .requestMatchers(HttpMethod.GET, "/api/genres/**").permitAll()
//                .requestMatchers(HttpMethod.PATCH, "/api/genres/**").hasRole("ADMIN")
//                .requestMatchers(HttpMethod.DELETE, "/api/genres/**").hasRole("ADMIN")
//                .requestMatchers(HttpMethod.POST, "/api/genres/**").hasRole("ADMIN")
//                .requestMatchers(HttpMethod.PUT, "/api/genres/**").hasRole("ADMIN")
//                // SONGS endpoint rules
//                .requestMatchers(HttpMethod.GET, "/api/catalog/songs/**").permitAll()
//                .requestMatchers(HttpMethod.PATCH, "/api/catalog/songs/**").hasRole("ADMIN")
//                .requestMatchers(HttpMethod.DELETE, "/api/catalog/songs/**").hasRole("ADMIN")
//                .requestMatchers(HttpMethod.POST, "/api/catalog/songs/**").hasRole("ADMIN")
//                .requestMatchers(HttpMethod.PUT, "/api/catalog/songs/**").hasRole("ADMIN")
//                // ALBUMS endpoint rules
//                .requestMatchers(HttpMethod.GET, "/api/albums/**").permitAll()
//                .requestMatchers(HttpMethod.PATCH, "/api/albums/**").hasRole("ADMIN")
//                .requestMatchers(HttpMethod.DELETE, "/api/albums/**").hasRole("ADMIN")
//                .requestMatchers(HttpMethod.POST, "/api/albums/**").hasRole("ADMIN")
//                .requestMatchers(HttpMethod.PUT, "/api/albums/**").hasRole("ADMIN")
//                // ARTISTS endpoint rules
//                .requestMatchers(HttpMethod.GET, "/api/artists/**").permitAll()
//                .requestMatchers(HttpMethod.PATCH, "/api/artists/**").hasRole("ADMIN")
//                .requestMatchers(HttpMethod.DELETE, "/api/artists/**").hasRole("ADMIN")
//                .requestMatchers(HttpMethod.POST, "/api/artists/**").hasRole("ADMIN")
//                .requestMatchers(HttpMethod.PUT, "/api/artists/**").hasRole("ADMIN")
                .anyRequest().authenticated());
        return http.build();
    }
}
