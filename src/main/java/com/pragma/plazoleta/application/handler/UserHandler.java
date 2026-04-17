package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.AuthDTO;
import com.pragma.plazoleta.application.dto.UserDTO;
import com.pragma.plazoleta.application.mapper.IAuthMapper;
import com.pragma.plazoleta.application.mapper.IUserMapper;
import com.pragma.plazoleta.domain.api.IUserServicePort;
import com.pragma.plazoleta.domain.model.Auth;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserHandler implements IUserHandler {

    private final IUserServicePort userServicePort;
    private final IUserMapper mapper;
    private final IAuthMapper authMapper;

    @Override
    public void createAdmin(UserDTO dto) {
        userServicePort.createAdmin(mapper.toUser(dto));
    }

    @Override
    public void creteOwner(UserDTO dto) {
        userServicePort.creteOwner(mapper.toUser(dto));
    }

    @Override
    public void createClient(UserDTO dto) {
        userServicePort.createClient(mapper.toUser(dto));
    }

    @Override
    public void createEmployee(UserDTO dto) {
        userServicePort.createEmployee(mapper.toUser(dto));
    }

    @Override
    public UserDTO getUserById(Long id) {
        return mapper.toUserDTO(userServicePort.getUserById(id));
    }


}
