package com.pragma.plazoleta.infrastructure.output.security.adapter;

import com.pragma.plazoleta.domain.api.IPasswordServicePort;
import com.pragma.plazoleta.infrastructure.constants.InfrastructureConstants;
import com.pragma.plazoleta.infrastructure.exception.InfrastructureException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PasswordServiceAdapter implements IPasswordServicePort {

    private final PasswordEncoder passwordEncoder;

    @Override
    public String encodePassword(String password) {
        if (password==null || password.isEmpty()) {
            throw new InfrastructureException(InfrastructureConstants.MSG_PASSWORDS_NULL, HttpStatus.BAD_REQUEST);
        }
        return passwordEncoder.encode(password);
    }

    @Override
    public boolean matches(String password, String hashedPassword) {
        if (password==null || password.isEmpty() || hashedPassword==null || hashedPassword.isEmpty()) {
            throw new InfrastructureException(InfrastructureConstants.MSG_PASSWORDS_NULL, HttpStatus.BAD_REQUEST);
        }
        return passwordEncoder.matches(password, hashedPassword);
    }
}
