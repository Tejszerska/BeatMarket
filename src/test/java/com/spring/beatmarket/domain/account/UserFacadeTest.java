package com.spring.beatmarket.domain.account;

import com.spring.beatmarket.domain.account.dto.UserRegistrationDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserFacadeTest {

    private final UserConformer userConformer = mock(UserConformer.class);
    private final UserRegisterer userRegisterer = mock(UserRegisterer.class);
    private final ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
    private final UserFacade userFacade = new UserFacade(userConformer, userRegisterer, eventPublisher);

    @Test
    @DisplayName("Publishes a registration event after registering a user")
    void publishes_registration_event() {
        // given
        UserRegistrationDto request = new UserRegistrationDto("new@example.com", "password");
        User user = User.builder().email(request.email()).build();
        when(userRegisterer.registerNewUser(request)).thenReturn(user);

        // when
        userFacade.registerNewUser(request);

        // then
        verify(eventPublisher).publishEvent(new UserRegisteredEvent(user));
    }

    @Test
    @DisplayName("Does not publish a registration event when registration fails")
    void skips_event_after_registration_failure() {
        // given
        UserRegistrationDto request = new UserRegistrationDto("existing@example.com", "password");
        when(userRegisterer.registerNewUser(request))
                .thenThrow(new UserExistsException());

        // when & then
        assertThatThrownBy(() -> userFacade.registerNewUser(request))
                .isInstanceOf(UserExistsException.class);
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("Delegates confirmation and returns its result")
    void delegates_confirmation() {
        // given
        String token = "confirmation-token";
        when(userConformer.confirmUser(token)).thenReturn(true);

        // when
        boolean confirmed = userFacade.confirmUser(token);

        // then
        assertThat(confirmed).isTrue();
        verify(userConformer).confirmUser(token);
    }

    @Test
    @DisplayName("Delegates registration for an external account")
    void delegates_external_registration() {
        // given
        String email = "external@example.com";

        // when
        userFacade.registerNewUser(email);

        // then
        verify(userRegisterer).registerNewUser(email);
    }
}
