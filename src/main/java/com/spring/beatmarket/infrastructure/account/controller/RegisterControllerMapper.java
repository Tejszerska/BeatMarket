package com.spring.beatmarket.infrastructure.account.controller;
import com.spring.beatmarket.domain.account.UserRequestDto;
import com.spring.beatmarket.infrastructure.account.controller.dto.RegisterUserRequestDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
interface RegisterControllerMapper {

   UserRequestDto mapFromRegisterUserRequestDtoToUserRequestDto(RegisterUserRequestDto dto);
}
