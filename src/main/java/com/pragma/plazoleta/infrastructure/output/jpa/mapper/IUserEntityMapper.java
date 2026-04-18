package com.pragma.plazoleta.infrastructure.output.jpa.mapper;


import com.pragma.plazoleta.domain.model.User;
import com.pragma.plazoleta.infrastructure.output.jpa.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IUserEntityMapper {

    @Mapping(target = "roleEntity.name", source = "role")
    UserEntity toEntity(User user);

    @Mapping(target = "role", source = "roleEntity.name")
    User toUser(UserEntity userEntity);
}
