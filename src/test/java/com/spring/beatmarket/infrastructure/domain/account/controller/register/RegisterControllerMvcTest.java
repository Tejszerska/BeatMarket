package com.spring.beatmarket.infrastructure.domain.account.controller.register;

import com.spring.beatmarket.domain.account.UserExistsException;
import com.spring.beatmarket.domain.account.UserFacade;
import com.spring.beatmarket.domain.account.UserNotFoundException;
import com.spring.beatmarket.domain.account.UserRepository;
import com.spring.beatmarket.domain.account.dto.UserRegistrationDto;
import com.spring.beatmarket.infrastructure.config.WebConfig;
import com.spring.beatmarket.infrastructure.security.SecurityMvcTestConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RegisterController.class)
@Import({WebConfig.class, SecurityMvcTestConfiguration.class})
class RegisterControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserFacade userFacade;

    @MockitoBean
    private RegisterControllerMapper mapper;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private AuthenticationSuccessHandler successHandler;

    @Test
    @DisplayName("Registers a valid account through the MVC endpoint")
    void registers_valid_account_through_mvc() throws Exception {
        // given
        RegisterUserRequest request = new RegisterUserRequest("new@example.com", "strong-password");
        UserRegistrationDto registration = new UserRegistrationDto(request.email(), request.password());
        when(mapper.toDomainRegistration(request)).thenReturn(registration);

        // when
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"new@example.com","password":"strong-password"}
                                """))

                // then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message")
                        .value("User created successfully. Confirmation email sent."));

        verify(userFacade).registerNewUser(registration);
    }

    @Test
    @DisplayName("Returns a conflict problem detail when the email already exists")
    void returns_conflict_problem_detail_for_existing_email() throws Exception {
        // given
        RegisterUserRequest request = new RegisterUserRequest("existing@example.com", "strong-password");
        UserRegistrationDto registration = new UserRegistrationDto(request.email(), request.password());
        when(mapper.toDomainRegistration(request)).thenReturn(registration);
        doThrow(new UserExistsException()).when(userFacade).registerNewUser(registration);

        // when
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"existing@example.com","password":"strong-password"}
                                """))

                // then
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Registration conflict"))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.detail")
                        .value("Registration could not be completed with the provided details."));

        verify(userFacade).registerNewUser(registration);
    }

    @ParameterizedTest
    @MethodSource("invalidRegistrationRequests")
    @DisplayName("Rejects an account registration with an invalid field value")
    void rejects_invalid_registration_through_mvc(String requestBody) throws Exception {
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(mapper, userFacade);
    }

    @Test
    @DisplayName("Confirms an account through the MVC endpoint")
    void confirms_account_through_mvc() throws Exception {
        // given
        when(userFacade.confirmUser("valid-token")).thenReturn(true);

        // when
        mockMvc.perform(get("/api/users/confirm").param("token", "valid-token"))

                // then
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("https://localhost:8443/swagger-ui/index.html"))
                .andExpect(jsonPath("$.message").value("Account activated. Redirecting to Swagger UI"));

        verify(userFacade).confirmUser("valid-token");
    }

    @Test
    @DisplayName("Returns not found when account confirmation fails")
    void returns_not_found_after_failed_confirmation() throws Exception {
        when(userFacade.confirmUser("invalid-token")).thenReturn(false);

        mockMvc.perform(get("/api/users/confirm").param("token", "invalid-token"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Confirmation failed. cannot login"));

        verify(userFacade).confirmUser("invalid-token");
    }

    @Test
    @DisplayName("Returns a not-found problem detail when the confirmation token is unknown")
    void returns_not_found_problem_detail_for_unknown_confirmation_token() throws Exception {
        // given
        when(userFacade.confirmUser("unknown-token")).thenThrow(new UserNotFoundException());

        // when
        mockMvc.perform(get("/api/users/confirm").param("token", "unknown-token"))

                // then
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Confirmation failed."))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("Invalid or expired confirmation token."));

        verify(userFacade).confirmUser("unknown-token");
    }

    private static Stream<Arguments> invalidRegistrationRequests() {
        return Stream.of(
                Arguments.of("{}"),
                Arguments.of("{\"email\":\"\",\"password\":\"strong-password\"}"),
                Arguments.of("{\"email\":\"invalid-email\",\"password\":\"strong-password\"}"),
                Arguments.of("{\"email\":\"valid@example.com\",\"password\":\"\"}"),
                Arguments.of("{\"email\":\"valid@example.com\",\"password\":\"short\"}"),
                Arguments.of("{\"email\":\"valid@example.com\"}")
        );
    }
}
