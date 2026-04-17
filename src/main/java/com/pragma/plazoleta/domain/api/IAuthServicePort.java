package com.pragma.plazoleta.domain.api;

import com.pragma.plazoleta.domain.model.Auth;

public interface IAuthServicePort {
    Auth login(Auth auth);
}
