package com.spring.beatmarket.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.beatmarket.domain.account.UserRepository;
import com.spring.beatmarket.infrastructure.security.jwt.JwtAuthTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
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
    ProblemDetailAuthenticationEntryPoint problemDetailAuthenticationEntryPoint(ObjectMapper objectMapper) {
        return new ProblemDetailAuthenticationEntryPoint(objectMapper);
    }

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationSuccessHandler successHandler,
            JwtAuthTokenFilter jwtAuthTokenFilter,
            ProblemDetailAuthenticationEntryPoint authenticationEntryPoint
    ) throws Exception {
        http.csrf(c -> c.disable());
        http.formLogin(c -> c.disable());
        http.httpBasic(c -> c.disable());

        http.oauth2Login(oauth2 -> oauth2
                .successHandler(successHandler)
        );
        http.exceptionHandling(c -> c.authenticationEntryPoint(authenticationEntryPoint));
        http.sessionManagement( c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(jwtAuthTokenFilter, UsernamePasswordAuthenticationFilter.class);

        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                        HttpMethod.POST,
                        "/api/users/register",
                        "/api/users/login",
                        "/api/users/logout"
                ).permitAll()
                .requestMatchers(HttpMethod.GET, "/api/users/confirm").permitAll()
                .requestMatchers(
                        "/oauth2/**",
                        "/login/oauth2/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/v3/api-docs",
                        "/swagger-resources/**",
                        "/webjars/**",
                        "/error"
                ).permitAll()
                .requestMatchers(HttpMethod.POST, "/api/webhook/payments").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/catalog/songs/*").hasAnyRole("CUSTOMER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/catalog/songs").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/catalog/genres/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/catalog/artists/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/catalog/albums/**").permitAll()
                .requestMatchers("/api/catalog/**").hasRole("ADMIN")
                .requestMatchers("/api/payments/**", "/api/licenses/**").authenticated()
                .anyRequest().authenticated());
        return http.build();
    }
}
