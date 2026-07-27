package com.spring.beatmarket.domain.account;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface UserMapper {
    UserDto mapFromEntityToUserDto(User user);
}
