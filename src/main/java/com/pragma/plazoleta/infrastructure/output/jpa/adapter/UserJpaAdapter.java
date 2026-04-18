package com.pragma.plazoleta.infrastructure.output.jpa.adapter;


import com.pragma.plazoleta.domain.model.User;
import com.pragma.plazoleta.domain.spi.IUserPersistencePort;
import com.pragma.plazoleta.infrastructure.exception.InfrastructureException;
import com.pragma.plazoleta.infrastructure.output.jpa.entity.RoleEntity;
import com.pragma.plazoleta.infrastructure.output.jpa.entity.UserEntity;
import com.pragma.plazoleta.infrastructure.output.jpa.mapper.IUserEntityMapper;
import com.pragma.plazoleta.infrastructure.output.jpa.repository.IRoleRepository;
import com.pragma.plazoleta.infrastructure.output.jpa.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserJpaAdapter implements IUserPersistencePort {

    private final UserRepository userRepository;
    private final IUserEntityMapper mapper;
    private final IRoleRepository roleRepository;

    @Override
    public void saveUser(User user) {
        RoleEntity role = roleRepository.findByName(user.getRole())
                .orElseThrow(() -> new InfrastructureException("Role not found: " + user.getRole(), HttpStatus.NOT_FOUND));
        UserEntity entity = mapper.toEntity(user);
        entity.setRoleEntity(role);
        userRepository.save(entity);
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id).map(mapper::toUser);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email).map(mapper::toUser);
    }


    @Override
    public Optional<User> findUserByCellphone(String cellphone) {
        return userRepository.findByPhoneNumber(cellphone).map(mapper::toUser);
    }
}