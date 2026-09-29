package com.pragma.plazoleta.application.usecase;

import com.pragma.plazoleta.domain.api.IPasswordServicePort;
import com.pragma.plazoleta.domain.constants.DomainConstants;
import com.pragma.plazoleta.domain.exception.ConflictException;
import com.pragma.plazoleta.domain.exception.DomainException;
import com.pragma.plazoleta.domain.exception.NotFoundException;
import com.pragma.plazoleta.domain.model.Role;
import com.pragma.plazoleta.domain.model.User;
import com.pragma.plazoleta.domain.spi.IUserPersistencePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    private static final String RAW_PASSWORD = "secreta123";
    private static final String ENCODED_PASSWORD = "$2a$10$hash";

    @Mock
    private IUserPersistencePort userPersistencePort;
    @Mock
    private IPasswordServicePort passwordServicePort;

    @InjectMocks
    private UserUseCase userUseCase;

    private static User validUser(LocalDate birthDate) {
        return User.builder()
                .name("Ana")
                .lastName("Pérez")
                .identificationNumber("1020304050")
                .phoneNumber("+573001234567")
                .birthDate(birthDate)
                .email("ana@correo.com")
                .password(RAW_PASSWORD)
                .build();
    }

    private static User adult() {
        return validUser(LocalDate.now().minusYears(30));
    }

    private static User minor() {
        return validUser(LocalDate.now().minusYears(17));
    }

    private void givenNoDuplicates(User user) {
        when(userPersistencePort.findUserByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userPersistencePort.findUserByCellphone(user.getPhoneNumber())).thenReturn(Optional.empty());
        when(passwordServicePort.encodePassword(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);
    }

    @Nested
    @DisplayName("Registro de propietario (creteOwner)")
    class CreateOwner {

        @Test
        @DisplayName("guarda un propietario mayor de edad con rol OWNER y la contraseña codificada")
        void savesAdultOwner() {
            // given
            User user = adult();
            givenNoDuplicates(user);

            // when
            userUseCase.creteOwner(user);

            // then
            assertThat(user.getRole()).isEqualTo(Role.OWNER);
            assertThat(user.getPassword()).isEqualTo(ENCODED_PASSWORD);
            verify(userPersistencePort).saveUser(user);
        }

        @Test
        @DisplayName("acepta un propietario que cumple 18 años hoy")
        void acceptsOwnerTurning18Today() {
            // given
            User user = validUser(LocalDate.now().minusYears(18));
            givenNoDuplicates(user);

            // when
            userUseCase.creteOwner(user);

            // then
            verify(userPersistencePort).saveUser(user);
        }

        @Test
        @DisplayName("rechaza un propietario menor de edad sin consultar duplicados ni guardar")
        void rejectsMinorOwner() {
            // given
            User user = minor();

            // when / then
            assertThatThrownBy(() -> userUseCase.creteOwner(user))
                    .isInstanceOf(DomainException.class)
                    .hasMessage(DomainConstants.MSG_UNDERAGE_USER);
            verify(userPersistencePort, never()).findUserByEmail(anyString());
            verify(passwordServicePort, never()).encodePassword(anyString());
            verify(userPersistencePort, never()).saveUser(any());
        }
    }

    @Nested
    @DisplayName("Registro de empleado (createEmployee)")
    class CreateEmployee {

        @Test
        @DisplayName("guarda el empleado con rol EMPLOYEE")
        void savesEmployee() {
            // given
            User user = adult();
            user.setRestaurantId(100L);
            givenNoDuplicates(user);

            // when
            userUseCase.createEmployee(user);

            // then
            assertThat(user.getRole()).isEqualTo(Role.EMPLOYEE);
            assertThat(user.getRestaurantId()).isEqualTo(100L);
            verify(userPersistencePort).saveUser(user);
        }

        @Test
        @DisplayName("no valida la edad del empleado (solo se valida para propietarios)")
        void doesNotValidateEmployeeAge() {
            // given
            User user = minor();
            givenNoDuplicates(user);

            // when
            userUseCase.createEmployee(user);

            // then
            verify(userPersistencePort).saveUser(user);
        }
    }

    @Nested
    @DisplayName("Registro de cliente (createClient)")
    class CreateClient {

        @Test
        @DisplayName("guarda el cliente con rol CLIENT sin validar la edad")
        void savesClientWithoutAgeValidation() {
            // given
            User user = minor();
            givenNoDuplicates(user);

            // when
            userUseCase.createClient(user);

            // then
            assertThat(user.getRole()).isEqualTo(Role.CLIENT);
            assertThat(user.getPassword()).isEqualTo(ENCODED_PASSWORD);
            verify(userPersistencePort).saveUser(user);
        }

        @Test
        @DisplayName("ignora el rol que venga en el usuario y fuerza CLIENT")
        void overridesIncomingRole() {
            // given
            User user = adult();
            user.setRole(Role.ADMIN);
            givenNoDuplicates(user);

            // when
            userUseCase.createClient(user);

            // then
            assertThat(user.getRole()).isEqualTo(Role.CLIENT);
        }
    }

    @Nested
    @DisplayName("Validaciones de formato")
    class FormatValidations {

        @ParameterizedTest(name = "teléfono \"{0}\"")
        @ValueSource(strings = {"12", "300-123-4567", "abc1234", "+57 300"})
        @DisplayName("rechaza teléfonos con formato inválido")
        void rejectsInvalidPhone(String phone) {
            // given
            User user = adult();
            user.setPhoneNumber(phone);

            // when / then
            assertThatThrownBy(() -> userUseCase.createClient(user))
                    .isInstanceOf(DomainException.class)
                    .hasMessage(DomainConstants.MSG_INVALID_CELLPHONE);
            verify(userPersistencePort, never()).saveUser(any());
        }

        @ParameterizedTest(name = "documento \"{0}\"")
        @ValueSource(strings = {"12", "10.203.040", "CC123456"})
        @DisplayName("rechaza documentos no numéricos o de menos de 3 dígitos")
        void rejectsInvalidIdentification(String document) {
            // given
            User user = adult();
            user.setIdentificationNumber(document);

            // when / then
            assertThatThrownBy(() -> userUseCase.createClient(user))
                    .isInstanceOf(DomainException.class)
                    .hasMessage("Invalid identification number");
            verify(userPersistencePort, never()).saveUser(any());
        }
    }

    @Nested
    @DisplayName("Duplicados")
    class Duplicates {

        @Test
        @DisplayName("rechaza un email ya registrado sin codificar ni guardar")
        void rejectsDuplicatedEmail() {
            // given
            User user = adult();
            when(userPersistencePort.findUserByEmail(user.getEmail())).thenReturn(Optional.of(new User()));

            // when / then
            assertThatThrownBy(() -> userUseCase.createClient(user))
                    .isInstanceOf(ConflictException.class)
                    .hasMessage(DomainConstants.MSG_EMAIL_ALREADY_EXISTS);
            verify(passwordServicePort, never()).encodePassword(anyString());
            verify(userPersistencePort, never()).saveUser(any());
        }

        @Test
        @DisplayName("rechaza un teléfono ya registrado sin codificar ni guardar")
        void rejectsDuplicatedPhone() {
            // given
            User user = adult();
            when(userPersistencePort.findUserByEmail(user.getEmail())).thenReturn(Optional.empty());
            when(userPersistencePort.findUserByCellphone(user.getPhoneNumber())).thenReturn(Optional.of(new User()));

            // when / then
            assertThatThrownBy(() -> userUseCase.creteOwner(user))
                    .isInstanceOf(ConflictException.class)
                    .hasMessage(DomainConstants.MSG_PHONE_ALREADY_EXISTS);
            verify(passwordServicePort, never()).encodePassword(anyString());
            verify(userPersistencePort, never()).saveUser(any());
        }
    }

    @Nested
    @DisplayName("getUserById")
    class GetUserById {

        @Test
        @DisplayName("devuelve el usuario cuando existe")
        void returnsUser() {
            // given
            User user = adult();
            when(userPersistencePort.findUserById(1L)).thenReturn(Optional.of(user));

            // when / then
            assertThat(userUseCase.getUserById(1L)).isSameAs(user);
        }

        @Test
        @DisplayName("lanza NotFoundException cuando no existe")
        void throwsWhenNotFound() {
            // given
            when(userPersistencePort.findUserById(1L)).thenReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> userUseCase.getUserById(1L))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(DomainConstants.MSG_USER_NOT_FOUND);
        }
    }
}
