package com.spring.beatmarket.infrastructure.account.controller.register;
import com.spring.beatmarket.domain.account.dto.UserRequestDto;
import com.spring.beatmarket.infrastructure.account.controller.register.dto.RegisterUserRequestDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
interface RegisterControllerMapper {

   UserRequestDto mapFromRegisterUserRequestDtoToUserRequestDto(RegisterUserRequestDto dto);
}
