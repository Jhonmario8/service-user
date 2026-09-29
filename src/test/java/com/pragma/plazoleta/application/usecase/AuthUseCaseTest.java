package com.pragma.plazoleta.application.usecase;

import com.pragma.plazoleta.domain.api.IPasswordServicePort;
import com.pragma.plazoleta.domain.api.ITokenServicePort;
import com.pragma.plazoleta.domain.constants.DomainConstants;
import com.pragma.plazoleta.domain.exception.UnauthorizedException;
import com.pragma.plazoleta.domain.model.Auth;
import com.pragma.plazoleta.domain.model.Role;
import com.pragma.plazoleta.domain.model.User;
import com.pragma.plazoleta.domain.spi.IUserPersistencePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthUseCaseTest {

    private static final String EMAIL = "ana@correo.com";
    private static final String RAW_PASSWORD = "secreta123";
    private static final String HASHED_PASSWORD = "$2a$10$hash";

    @Mock
    private IUserPersistencePort userPersistencePort;
    @Mock
    private IPasswordServicePort passwordServicePort;
    @Mock
    private ITokenServicePort tokenServicePort;

    @InjectMocks
    private AuthUseCase authUseCase;

    private static User storedUser() {
        return User.builder().id(1L).email(EMAIL).password(HASHED_PASSWORD).role(Role.CLIENT).build();
    }

    @Test
    @DisplayName("con credenciales válidas devuelve el token y limpia email y contraseña")
    void loginWithValidCredentials() {
        // given
        User user = storedUser();
        when(userPersistencePort.findUserByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(passwordServicePort.matches(RAW_PASSWORD, HASHED_PASSWORD)).thenReturn(true);
        when(tokenServicePort.generateToken(user)).thenReturn("jwt-token");

        // when
        Auth result = authUseCase.login(new Auth(EMAIL, RAW_PASSWORD, null));

        // then
        assertThat(result.getToken()).isEqualTo("jwt-token");
        assertThat(result.getEmail()).isNull();
        assertThat(result.getPassword()).isNull();
    }

    @Test
    @DisplayName("con contraseña incorrecta falla con 'Invalid credentials' y no genera token")
    void loginWithWrongPassword() {
        // given
        when(userPersistencePort.findUserByEmail(EMAIL)).thenReturn(Optional.of(storedUser()));
        when(passwordServicePort.matches("otra", HASHED_PASSWORD)).thenReturn(false);

        // when / then
        assertThatThrownBy(() -> authUseCase.login(new Auth(EMAIL, "otra", null)))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage(DomainConstants.MSG_INVALID_CREDENTIALS);
        verify(tokenServicePort, never()).generateToken(any());
    }

    @Test
    @DisplayName("con email no registrado falla con 'Invalid credentials' y no genera token")
    void loginWithUnknownEmail() {
        // given
        when(userPersistencePort.findUserByEmail(EMAIL)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> authUseCase.login(new Auth(EMAIL, RAW_PASSWORD, null)))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage(DomainConstants.MSG_INVALID_CREDENTIALS);
        verify(passwordServicePort, never()).matches(anyString(), anyString());
        verify(tokenServicePort, never()).generateToken(any());
    }

    @Test
    @DisplayName("usuario inexistente y contraseña incorrecta producen la misma excepción y mensaje")
    void unknownEmailAndWrongPasswordAreIndistinguishable() {
        // given
        when(userPersistencePort.findUserByEmail("nadie@correo.com")).thenReturn(Optional.empty());
        when(userPersistencePort.findUserByEmail(EMAIL)).thenReturn(Optional.of(storedUser()));
        when(passwordServicePort.matches("otra", HASHED_PASSWORD)).thenReturn(false);

        // when
        Throwable unknownEmail = catchThrowable(() -> authUseCase.login(new Auth("nadie@correo.com", RAW_PASSWORD, null)));
        Throwable wrongPassword = catchThrowable(() -> authUseCase.login(new Auth(EMAIL, "otra", null)));

        // then
        assertThat(unknownEmail).isExactlyInstanceOf(UnauthorizedException.class);
        assertThat(wrongPassword).isExactlyInstanceOf(UnauthorizedException.class);
        assertThat(unknownEmail).hasMessage(wrongPassword.getMessage());
    }
}
