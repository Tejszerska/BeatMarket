package com.spring.beatmarket.infrastructure.domain.account.controller.register;

import com.spring.beatmarket.domain.account.UserExistsException;
import com.spring.beatmarket.domain.account.UserFacade;
import com.spring.beatmarket.domain.account.dto.UserRequestDto;
import com.spring.beatmarket.infrastructure.domain.account.controller.register.dto.RegisterUserRequestDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.stream.Stream;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RegisterControllerTest {

    private final UserFacade userFacade = mock(UserFacade.class);
    private final RegisterControllerMapper mapper = mock(RegisterControllerMapper.class);
    private MockMvc mockMvc;
    private LocalValidatorFactoryBean validator;

    @BeforeEach
    void setUp() {
        validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(new RegisterController(userFacade, mapper))
                .setValidator(validator)
                .build();
    }

    @AfterEach
    void tearDown() {
        validator.close();
    }

    @Test
    @DisplayName("Registers a valid user and returns a created response")
    void registers_valid_user() throws Exception {
        // given
        RegisterUserRequestDto request = new RegisterUserRequestDto("new@example.com", "strong-password");
        UserRequestDto mappedRequest = new UserRequestDto(request.email(), request.password());
        when(mapper.mapFromRegisterUserRequestDtoToUserRequestDto(request)).thenReturn(mappedRequest);

        // when & then
        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"new@example.com","password":"strong-password"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message")
                        .value("User created successfully. Confirmation email sent."));

        verify(userFacade).registerNewUser(mappedRequest);
    }

    @Test
    @DisplayName("Returns a conflict response when the email already exists")
    void returns_conflict_for_duplicate_email() throws Exception {
        // given
        RegisterUserRequestDto request = new RegisterUserRequestDto("existing@example.com", "strong-password");
        UserRequestDto mappedRequest = new UserRequestDto(request.email(), request.password());
        when(mapper.mapFromRegisterUserRequestDtoToUserRequestDto(request)).thenReturn(mappedRequest);
        doThrow(mock(UserExistsException.class)).when(userFacade).registerNewUser(mappedRequest);

        // when & then
        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"existing@example.com","password":"strong-password"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("User with this email already exists."));

        verify(userFacade).registerNewUser(mappedRequest);
    }

    @ParameterizedTest
    @MethodSource("invalidRegistrationRequests")
    @DisplayName("Rejects a registration request with an invalid field value")
    void rejects_invalid_registration_request(String requestBody) throws Exception {
        // when & then
        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(mapper, userFacade);
    }

    @Test
    @DisplayName("Redirects after confirming an account")
    void redirects_after_confirmation() throws Exception {
        // given
        when(userFacade.confirmUser("valid-token")).thenReturn(true);

        // when & then
        mockMvc.perform(get("/users/confirm").param("token", "valid-token"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("https://localhost:8443/swagger-ui/index.html"))
                .andExpect(jsonPath("$.message")
                        .value("Account activated. Redirecting to Swagger UI"));

        verify(userFacade).confirmUser("valid-token");
    }

    @Test
    @DisplayName("Returns not found when account confirmation fails")
    void returns_not_found_after_failed_confirmation() throws Exception {
        // given
        when(userFacade.confirmUser("invalid-token")).thenReturn(false);

        // when & then
        mockMvc.perform(get("/users/confirm").param("token", "invalid-token"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Confirmation failed. cannot login"));

        verify(userFacade).confirmUser("invalid-token");
    }

    private static Stream<Arguments> invalidRegistrationRequests() {
        return Stream.of(
                Arguments.of("""
                        {}
                        """),
                Arguments.of("""
                        {"email":"","password":"strong-password"}
                        """),
                Arguments.of("""
                        {"email":"invalid-email","password":"strong-password"}
                        """),
                Arguments.of("""
                        {"email":"valid@example.com","password":""}
                        """),
                Arguments.of("""
                        {"email":"valid@example.com","password":"short"}
                        """),
                Arguments.of("""
                        {"email":"valid@example.com"}
                        """)
        );
    }
}
