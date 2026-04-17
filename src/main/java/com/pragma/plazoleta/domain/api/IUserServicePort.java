package com.pragma.plazoleta.domain.api;


import com.pragma.plazoleta.domain.model.User;

public interface IUserServicePort {
    void createAdmin(User user);

    void creteOwner(User user);

    void createClient(User user);

    void createEmployee(User user);

    User getUserById(Long id);


}
