package com.pragma.plazoleta.application.usecase;

import com.pragma.plazoleta.domain.api.IPasswordServicePort;
import com.pragma.plazoleta.domain.api.ITokenServicePort;
import com.pragma.plazoleta.domain.api.IUserServicePort;
import com.pragma.plazoleta.domain.constants.DomainConstants;
import com.pragma.plazoleta.domain.exception.DomainException;
import com.pragma.plazoleta.domain.model.Role;
import com.pragma.plazoleta.domain.model.User;
import com.pragma.plazoleta.domain.spi.IUserPersistencePort;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserUseCase implements IUserServicePort {

    private final IUserPersistencePort userPersistencePort;
    private final IPasswordServicePort passwordServicePort;
    private final ITokenServicePort tokenServicePort;

    @Override
    public void createAdmin(User user) {
        user.setPassword(passwordServicePort.encodePassword(user.getPassword()));
        user.setRole(Role.ADMIN);
        userPersistencePort.saveUser(user);
    }

    @Override
    public void creteOwner(User user) {
        user.setPassword(passwordServicePort.encodePassword(user.getPassword()));
        user.setRole(Role.OWNER);
        userPersistencePort.saveUser(user);
    }

    @Override
    public void createClient(User user) {
        user.setPassword(passwordServicePort.encodePassword(user.getPassword()));
        user.setRole(Role.CLIENT);
        userPersistencePort.saveUser(user);
    }

    @Override
    public void createEmployee(User user) {
        user.setPassword(passwordServicePort.encodePassword(user.getPassword()));
        user.setRole(Role.EMPLOYEE);
        userPersistencePort.saveUser(user);
    }

    @Override
    public User getUserById(Long id) {
        return userPersistencePort.findUserById(id)
                .orElseThrow(() -> new DomainException(DomainConstants.MSG_USER_NOT_FOUND, HttpStatus.NOT_FOUND));
    }



}
