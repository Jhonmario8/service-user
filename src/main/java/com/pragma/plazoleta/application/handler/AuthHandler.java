package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.AuthDTO;
import com.pragma.plazoleta.application.mapper.IAuthMapper;
import com.pragma.plazoleta.domain.api.IAuthServicePort;
import com.pragma.plazoleta.domain.model.Auth;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthHandler implements IAuthHandler {

        private final IAuthServicePort authServicePort;
        private final IAuthMapper authMapper;

    @Override
    public AuthDTO login(AuthDTO authDTO) {
        Auth authEntity = authMapper.toDomain(authDTO);
        Auth authResult = authServicePort.login(authEntity);
        return authMapper.toDTO(authResult);
    }

}
