package com.spring.beatmarket.infrastructure.domain.account.controller.login;

import com.spring.beatmarket.infrastructure.security.jwt.dto.JwtGenerationDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
interface LoginControllerMapper {
    JwtGenerationDto mapFromLoginRequestDtoToJwtGenerationDto(LoginRequestDto dto);
}
