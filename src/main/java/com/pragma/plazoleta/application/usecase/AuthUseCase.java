package com.pragma.plazoleta.application.usecase;

import com.pragma.plazoleta.domain.api.IAuthServicePort;
import com.pragma.plazoleta.domain.api.IPasswordServicePort;
import com.pragma.plazoleta.domain.api.ITokenServicePort;
import com.pragma.plazoleta.domain.constants.DomainConstants;
import com.pragma.plazoleta.domain.exception.ConflictException;
import com.pragma.plazoleta.domain.exception.NotFoundException;
import com.pragma.plazoleta.domain.model.Auth;
import com.pragma.plazoleta.domain.model.User;
import com.pragma.plazoleta.domain.spi.IUserPersistencePort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthUseCase implements IAuthServicePort {

    private final IUserPersistencePort userPersistencePort;
    private final IPasswordServicePort passwordServicePort;
    private final ITokenServicePort tokenServicePort;

    @Override
    public Auth login(Auth auth){
        User user = userPersistencePort.findUserByEmail(auth.getEmail())
                .orElseThrow(() -> new NotFoundException(DomainConstants.MSG_USER_NOT_FOUND));

        if (passwordServicePort.matches(auth.getPassword(), user.getPassword())) {
            auth.setToken(tokenServicePort.generateToken(user));
            auth.setPassword(null);
            auth.setEmail(null);
            auth.setRole(user.getRole());
            return auth;
        } else {
            throw new ConflictException(DomainConstants.MSG_INVALID_CREDENTIALS);
        }
    }
}
