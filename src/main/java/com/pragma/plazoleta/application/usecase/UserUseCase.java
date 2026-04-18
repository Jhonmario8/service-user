package com.pragma.plazoleta.application.usecase;

import com.pragma.plazoleta.domain.api.IPasswordServicePort;
import com.pragma.plazoleta.domain.api.IUserServicePort;
import com.pragma.plazoleta.domain.constants.DomainConstants;
import com.pragma.plazoleta.domain.exception.ConflictException;
import com.pragma.plazoleta.domain.exception.NotFoundException;
import com.pragma.plazoleta.domain.model.Role;
import com.pragma.plazoleta.domain.model.User;
import com.pragma.plazoleta.domain.spi.IUserPersistencePort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserUseCase implements IUserServicePort {

    private final IUserPersistencePort userPersistencePort;
    private final IPasswordServicePort passwordServicePort;


    @Override
    public void createAdmin(User user) {
        createUser(user, Role.ADMIN, true);
    }

    @Override
    public void creteOwner(User user) {
        createUser(user, Role.OWNER, true);
    }

    @Override
    public void createClient(User user) {
        createUser(user, Role.CLIENT, false);
    }

    @Override
    public void createEmployee(User user) {
        createUser(user, Role.EMPLOYEE, false);
    }

    @Override
    public User getUserById(Long id) {
        return userPersistencePort.findUserById(id)
                .orElseThrow(() -> new NotFoundException(DomainConstants.MSG_USER_NOT_FOUND));
    }

    private void createUser(User user, Role role, boolean validateAge) {
        user.setRole(role);
        user.validate(validateAge);
        validateUniqueness(user);
        user.encodePassword(passwordServicePort);
        userPersistencePort.saveUser(user);
    }


    private void validateUniqueness(User user) {
        if (userPersistencePort.findUserByEmail(user.getEmail()).isPresent()) {
            throw new ConflictException(DomainConstants.MSG_EMAIL_ALREADY_EXISTS);
        }
        if (userPersistencePort.findUserByCellphone(user.getPhoneNumber()).isPresent()) {
            throw new ConflictException(DomainConstants.MSG_PHONE_ALREADY_EXISTS);
        }
    }
}
