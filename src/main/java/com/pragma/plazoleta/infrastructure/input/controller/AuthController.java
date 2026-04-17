package com.pragma.plazoleta.infrastructure.input.controller;

import com.pragma.plazoleta.application.dto.AuthDTO;
import com.pragma.plazoleta.application.handler.IAuthHandler;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final IAuthHandler authHandler;

    @PostMapping("/login")
    public AuthDTO login(@RequestBody AuthDTO authDTO) {
        return authHandler.login(authDTO);
    }
}
