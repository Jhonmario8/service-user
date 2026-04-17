package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.AuthDTO;
import com.pragma.plazoleta.application.dto.UserDTO;
import jakarta.validation.Valid;

public interface IUserHandler {
    void createAdmin(@Valid UserDTO dto);
    void creteOwner(@Valid UserDTO dto);
    void createClient(@Valid UserDTO dto);
    void createEmployee(@Valid UserDTO dto);
    UserDTO getUserById(Long id);

}
