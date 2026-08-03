package com.spring.beatmarket.domain.licensing;

import com.spring.beatmarket.domain.licensing.dto.SongPriceDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
interface SongPriceMapper {
    SongPriceDto mapFromEntityToSongPriceDto (SongPrice songPrice);

}
