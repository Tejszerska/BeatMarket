package com.spring.beatmarket.infrastructure.domain.account.controller.login;

import com.spring.beatmarket.infrastructure.security.jwt.CookieService;
import com.spring.beatmarket.infrastructure.security.jwt.dto.JwtGenerationDto;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LoginControllerTest {

    private final CookieService cookieService = mock(CookieService.class);
    private final LoginControllerMapper mapper = mock(LoginControllerMapper.class);
    private MockMvc mockMvc;
    private LocalValidatorFactoryBean validator;

    @BeforeEach
    void setUp() {
        validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(new LoginController(cookieService, mapper))
                .setValidator(validator)
                .build();
    }

    @AfterEach
    void tearDown() {
        validator.close();
    }

    @Test
    @DisplayName("Authenticates valid credentials and adds an access cookie")
    void authenticates_valid_credentials() throws Exception {
        // given
        LoginRequest request = new LoginRequest("user@example.com", "password");
        JwtGenerationDto credentials = new JwtGenerationDto(request.email(), request.password());
        Cookie cookie = new Cookie("accessToken", "signed-token");
        when(mapper.mapFromLoginRequestToJwtGenerationDto(request)).thenReturn(credentials);
        when(cookieService.createAccessTokenCookie(credentials, 360)).thenReturn(cookie);

        // when & then
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"user@example.com","password":"password"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful."))
                .andExpect(cookie().value("accessToken", "signed-token"));

        verify(cookieService).createAccessTokenCookie(credentials, 360);
    }

    @Test
    @DisplayName("Propagates authentication failure through MVC")
    void propagates_authentication_failure() {
        // given
        LoginRequest request = new LoginRequest("user@example.com", "incorrect");
        JwtGenerationDto credentials = new JwtGenerationDto(request.email(), request.password());
        when(mapper.mapFromLoginRequestToJwtGenerationDto(request)).thenReturn(credentials);
        when(cookieService.createAccessTokenCookie(credentials, 360))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // when
        Throwable exception = catchThrowable(() -> mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"email":"user@example.com","password":"incorrect"}
                        """)));

        // then
        assertThat(exception).hasRootCauseInstanceOf(BadCredentialsException.class);
        verify(cookieService).createAccessTokenCookie(credentials, 360);
    }

    @Test
    @DisplayName("Rejects login credentials with a blank field")
    void rejects_blank_login_credentials() throws Exception {
        // when & then
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"","password":"password"}
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(mapper, cookieService);
    }

    @Test
    @DisplayName("Clears the access cookie when logging out")
    void clears_access_cookie_on_logout() throws Exception {
        // given
        Cookie clearedCookie = new Cookie("accessToken", null);
        clearedCookie.setMaxAge(0);
        when(cookieService.clearTokenCookie()).thenReturn(clearedCookie);

        // when & then
        mockMvc.perform(post("/users/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logged out successfully."))
                .andExpect(cookie().exists("accessToken"))
                .andExpect(cookie().maxAge("accessToken", 0));

        verify(cookieService).clearTokenCookie();
    }
}
