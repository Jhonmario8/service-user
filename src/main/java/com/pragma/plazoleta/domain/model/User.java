package com.pragma.plazoleta.domain.model;

import lombok.*;

import java.time.LocalDate;


@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long id;
    private String name;
    private String lastName;
    private String identificationNumber;
    private String phoneNumber;
    private LocalDate birthDate;
    private String email;
    private String password;
    private Role role;
}
