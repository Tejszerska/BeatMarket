package com.spring.beatmarket.infrastructure.domain.account.controller.register;
import com.spring.beatmarket.domain.account.dto.UserRegistrationDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
interface RegisterControllerMapper {

   UserRegistrationDto toDomainRegistration(RegisterUserRequest dto);
}
