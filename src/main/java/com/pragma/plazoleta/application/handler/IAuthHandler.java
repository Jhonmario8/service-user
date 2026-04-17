package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.AuthDTO;
import com.pragma.plazoleta.domain.model.Auth;
import jakarta.validation.Valid;

public interface IAuthHandler {

    AuthDTO login(@Valid AuthDTO authDTO);
}
