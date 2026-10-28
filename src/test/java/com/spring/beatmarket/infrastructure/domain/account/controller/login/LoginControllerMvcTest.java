package com.spring.beatmarket.infrastructure.domain.account.controller.login;

import com.spring.beatmarket.domain.account.UserRepository;
import com.spring.beatmarket.infrastructure.config.WebConfig;
import com.spring.beatmarket.infrastructure.security.SecurityMvcTestConfiguration;
import com.spring.beatmarket.infrastructure.security.jwt.CookieService;
import com.spring.beatmarket.infrastructure.security.jwt.dto.JwtGenerationDto;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LoginController.class)
@Import({WebConfig.class, SecurityMvcTestConfiguration.class})
class LoginControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CookieService cookieService;

    @MockitoBean
    private LoginControllerMapper mapper;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private AuthenticationSuccessHandler successHandler;

    @Test
    @DisplayName("Issues an access cookie after a successful MVC login")
    void issues_access_cookie_after_login() throws Exception {
        // given
        LoginRequest request = new LoginRequest("user@example.com", "password");
        JwtGenerationDto credentials = new JwtGenerationDto(request.email(), request.password());
        Cookie accessCookie = new Cookie("accessToken", "signed-token");
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);
        accessCookie.setPath("/");
        when(mapper.mapFromLoginRequestToJwtGenerationDto(request)).thenReturn(credentials);
        when(cookieService.createAccessTokenCookie(credentials, 360)).thenReturn(accessCookie);

        // when
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"user@example.com","password":"password"}
                                """))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful."))
                .andExpect(cookie().value("accessToken", "signed-token"))
                .andExpect(cookie().httpOnly("accessToken", true))
                .andExpect(cookie().secure("accessToken", true));

        verify(cookieService).createAccessTokenCookie(credentials, 360);
    }

    @Test
    @DisplayName("Returns an unauthorized problem detail for invalid login credentials")
    void returns_unauthorized_problem_detail_for_invalid_credentials() throws Exception {
        // given
        LoginRequest request = new LoginRequest("user@example.com", "incorrect");
        JwtGenerationDto credentials = new JwtGenerationDto(request.email(), request.password());
        when(mapper.mapFromLoginRequestToJwtGenerationDto(request)).thenReturn(credentials);
        when(cookieService.createAccessTokenCookie(credentials, 360))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // when
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"user@example.com","password":"incorrect"}
                                """))

                // then
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Authentication failed"))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.detail").value("Invalid email or password."));

        verify(cookieService).createAccessTokenCookie(credentials, 360);
    }

    @Test
    @DisplayName("Rejects login with a blank credential through the MVC endpoint")
    void rejects_blank_login_credential() throws Exception {
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"","password":"password"}
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(mapper, cookieService);
    }

    @Test
    @DisplayName("Clears the access cookie through the MVC logout endpoint")
    void clears_access_cookie_through_mvc() throws Exception {
        // given
        Cookie clearedCookie = new Cookie("accessToken", null);
        clearedCookie.setHttpOnly(true);
        clearedCookie.setSecure(true);
        clearedCookie.setPath("/");
        clearedCookie.setMaxAge(0);
        when(cookieService.clearTokenCookie()).thenReturn(clearedCookie);

        // when
        mockMvc.perform(post("/api/users/logout"))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logged out successfully."))
                .andExpect(cookie().exists("accessToken"))
                .andExpect(cookie().maxAge("accessToken", 0));

        verify(cookieService).clearTokenCookie();
        verify(mapper, never()).mapFromLoginRequestToJwtGenerationDto(org.mockito.ArgumentMatchers.any());
    }
}
