package com.pragma.plazoleta.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class TokenData {
    private Long userId;
    private String role;
    private Date expiration;
    private String email;
}
