package com.pragma.plazoleta.domain.api;


import com.pragma.plazoleta.domain.model.User;

import java.util.Map;

public interface ITokenServicePort {

    String generateToken(User user);
    Map<String, Object> validateToken(String token);
}
