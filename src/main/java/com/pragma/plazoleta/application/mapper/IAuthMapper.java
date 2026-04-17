package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.AuthDTO;
import com.pragma.plazoleta.domain.model.Auth;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IAuthMapper {

    Auth toDomain(AuthDTO dto);

    AuthDTO toDTO(Auth auth);
}
