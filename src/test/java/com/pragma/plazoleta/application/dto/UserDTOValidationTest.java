package com.pragma.plazoleta.application.dto;

import com.pragma.plazoleta.application.constants.ApplicationConstants;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Valida las anotaciones de Bean Validation de UserDTO con el Validator de Hibernate,
 * sin levantar el contexto de Spring.
 */
class UserDTOValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    private static UserDTO validDto() {
        return UserDTO.builder()
                .name("Ana")
                .lastName("Pérez")
                .identificationNumber("1020304050")
                .phoneNumber("+573001234567")
                .birthDate(LocalDate.of(1995, 5, 20))
                .email("ana@correo.com")
                .password("secreta123")
                .build();
    }

    private static Set<String> messages(UserDTO dto) {
        return validator.validate(dto).stream()
                .map(ConstraintViolation::getMessage)
                .collect(java.util.stream.Collectors.toSet());
    }

    @Test
    @DisplayName("un DTO completo y bien formado no tiene violaciones")
    void validDtoHasNoViolations() {
        assertThat(validator.validate(validDto())).isEmpty();
    }

    @ParameterizedTest(name = "email \"{0}\"")
    @ValueSource(strings = {"ana", "ana@", "@correo.com", "ana correo@x.com"})
    @DisplayName("rechaza emails con formato inválido")
    void rejectsInvalidEmail(String email) {
        // given
        UserDTO dto = validDto();
        dto.setEmail(email);

        // when / then
        assertThat(messages(dto)).contains(ApplicationConstants.EMAIL_SHOULD_BE_VALID);
    }

    @Test
    @DisplayName("rechaza email vacío")
    void rejectsBlankEmail() {
        UserDTO dto = validDto();
        dto.setEmail("");

        assertThat(messages(dto)).contains(ApplicationConstants.EMAIL_CANNOT_BE_BLANK);
    }

    @Test
    @DisplayName("rechaza contraseña vacía")
    void rejectsBlankPassword() {
        UserDTO dto = validDto();
        dto.setPassword("  ");

        assertThat(messages(dto)).containsExactly(ApplicationConstants.PASSWORD_CANNOT_BE_BLANK);
    }

    @Test
    @DisplayName("rechaza fecha de nacimiento nula")
    void rejectsNullBirthDate() {
        UserDTO dto = validDto();
        dto.setBirthDate(null);

        assertThat(messages(dto)).containsExactly(ApplicationConstants.BIRTH_DATE_CANNOT_BE_NULL);
    }

    @Test
    @DisplayName("rechaza nombre, apellido, documento y teléfono vacíos")
    void rejectsBlankRequiredFields() {
        UserDTO dto = validDto();
        dto.setName(" ");
        dto.setLastName(null);
        dto.setIdentificationNumber("");
        dto.setPhoneNumber(null);

        assertThat(messages(dto)).containsExactlyInAnyOrder(
                ApplicationConstants.NAME_CANNOT_BE_BLANK,
                ApplicationConstants.LAST_NAME_CANNOT_BE_BLANK,
                ApplicationConstants.IDENTIFICATION_NUMBER_CANNOT_BE_BLANK,
                ApplicationConstants.PHONE_NUMBER_CANNOT_BE_BLANK
        );
    }
}
