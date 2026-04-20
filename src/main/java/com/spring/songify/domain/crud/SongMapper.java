package com.spring.songify.domain.crud;

import com.spring.songify.domain.crud.dto.SongDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        uses = {GenreMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
interface SongMapper {
    SongDto mapFromEntityToSongDto (Song song);
    void updateSongFromDto(SongDto dto, @MappingTarget Song song);
}
