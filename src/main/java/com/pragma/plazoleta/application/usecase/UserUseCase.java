package com.pragma.plazoleta.application.usecase;

import com.pragma.plazoleta.domain.api.IPasswordServicePort;
import com.pragma.plazoleta.domain.api.IUserServicePort;
import com.pragma.plazoleta.domain.constants.DomainConstants;
import com.pragma.plazoleta.domain.exception.DomainException;
import com.pragma.plazoleta.domain.model.Role;
import com.pragma.plazoleta.domain.model.User;
import com.pragma.plazoleta.domain.spi.IUserPersistencePort;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

import static com.pragma.plazoleta.application.util.UserValidator.validateCellphone;
import static com.pragma.plazoleta.application.util.UserValidator.validateDocument;


@Service
@AllArgsConstructor
public class UserUseCase implements IUserServicePort {

    private final IUserPersistencePort userPersistencePort;
    private final IPasswordServicePort passwordServicePort;


    @Override
    public void createAdmin(User user) {
        user.setPassword(passwordServicePort.encodePassword(user.getPassword()));
        user.setRole(Role.ADMIN);
        validateUser(user, true);
        userPersistencePort.saveUser(user);
    }

    @Override
    public void creteOwner(User user) {
        user.setPassword(passwordServicePort.encodePassword(user.getPassword()));
        user.setRole(Role.OWNER);
        validateUser(user, true);
        userPersistencePort.saveUser(user);
    }

    @Override
    public void createClient(User user) {
        user.setPassword(passwordServicePort.encodePassword(user.getPassword()));
        user.setRole(Role.CLIENT);
        validateUser(user,false);
        userPersistencePort.saveUser(user);
    }

    @Override
    public void createEmployee(User user) {
        user.setPassword(passwordServicePort.encodePassword(user.getPassword()));
        user.setRole(Role.EMPLOYEE);
        validateUser(user, false);
        userPersistencePort.saveUser(user);
    }

    @Override
    public User getUserById(Long id) {
        return userPersistencePort.findUserById(id)
                .orElseThrow(() -> new DomainException(DomainConstants.MSG_USER_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    private void validateUser(User userEntity, boolean validateAge) {
        if (!validateCellphone(userEntity.getPhoneNumber())) {
            throw new DomainException(DomainConstants.MSG_INVALID_CELLPHONE, HttpStatus.BAD_REQUEST);
        }
        if (!validateDocument(userEntity.getIdentificationNumber())) {
            throw new DomainException(DomainConstants.MSG_INVALID_DOCUMENT, HttpStatus.BAD_REQUEST);
        }
        if (validateAge && Period.between(userEntity.getBirthDate(), LocalDate.now()).getYears() < 18) {
            throw new DomainException(DomainConstants.MSG_UNDERAGE_USER, HttpStatus.BAD_REQUEST);
        }
        if (userPersistencePort.findUserByEmail(userEntity.getEmail()).isPresent()) {
            throw new DomainException(DomainConstants.MSG_EMAIL_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }
        if (userPersistencePort.findUserByCellphone(userEntity.getPhoneNumber()).isPresent()) {
            throw new DomainException(DomainConstants.MSG_PHONE_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }
    }

}
