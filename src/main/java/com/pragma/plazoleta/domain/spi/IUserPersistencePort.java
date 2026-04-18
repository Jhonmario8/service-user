package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.model.User;

import java.util.Optional;


public interface IUserPersistencePort {
    void saveUser(User user);

    Optional<User> findUserById(Long id);

    Optional<User> findUserByEmail(String email);


    Optional<User> findUserByCellphone(String cellphone);
}
