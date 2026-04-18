package com.pragma.plazoleta.domain.model;

import com.pragma.plazoleta.domain.api.IPasswordServicePort;
import com.pragma.plazoleta.domain.exception.DomainException;

import lombok.*;


import java.time.LocalDate;
import java.time.Period;


@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private static final String CELLPHONE_PATTERN = "^\\+?\\d{3,}$";
    private static final String DOCUMENT_PATTERN = "^\\d{3,}$";

    private Long id;
    private String name;
    private String lastName;
    private String identificationNumber;
    private String phoneNumber;
    private LocalDate birthDate;
    private String email;
    private String password;
    private Role role;

    public void encodePassword(IPasswordServicePort passwordServicePort) {
        this.password = passwordServicePort.encodePassword(this.password);
    }

    public void validate(boolean validateAge) {
        if (validateAge && Period.between(this.birthDate, LocalDate.now()).getYears() < 18) {
            throw new DomainException("User must be at least 18 years old");
        }
        if (!this.phoneNumber.matches(CELLPHONE_PATTERN)){
            throw new DomainException("Invalid cellphone number");
        }
        if (!this.identificationNumber.matches(DOCUMENT_PATTERN)){
            throw new DomainException("Invalid identification number");
        }
    }
}
