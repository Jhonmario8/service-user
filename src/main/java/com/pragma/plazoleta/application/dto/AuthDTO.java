package com.pragma.plazoleta.application.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.pragma.plazoleta.application.constants.ApplicationConstants;
import com.pragma.plazoleta.domain.model.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AuthDTO {
    @NotBlank(message = ApplicationConstants.EMAIL_CANNOT_BE_BLANK)
    private String email;
    @NotBlank(message = ApplicationConstants.PASSWORD_CANNOT_BE_BLANK)
    private String password;
    private String token;
    private Role role;
}
