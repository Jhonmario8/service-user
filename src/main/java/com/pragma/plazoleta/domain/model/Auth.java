package com.pragma.plazoleta.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Auth {
    private String email;
    private String password;
    private String token;
    private Role role;
}
