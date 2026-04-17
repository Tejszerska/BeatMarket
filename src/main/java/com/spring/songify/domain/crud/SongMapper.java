package com.spring.songify.domain.crud;

import com.spring.songify.domain.crud.dto.SongDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        uses = {GenreMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface SongMapper {
    SongDto mapFromEntityToSongDto (Song song);
}
