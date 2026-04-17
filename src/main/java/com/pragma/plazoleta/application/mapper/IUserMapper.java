package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.UserDTO;
import com.pragma.plazoleta.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IUserMapper {
    User toUser(UserDTO dto);
    UserDTO toUserDTO(User user);
}
