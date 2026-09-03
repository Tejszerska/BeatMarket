package com.spring.beatmarket.infrastructure.domain.catalog.controller.album;

import com.spring.beatmarket.domain.catalog.dto.AlbumDto;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Slice;

@Mapper(componentModel = "spring")
public interface AlbumControllerMapper {
    AlbumApiDto.SummaryResponse toSummaryResponse (AlbumDto.Summary dto);

    default AlbumApiDto.GetAllResponse toGetAllResponse(Slice<AlbumDto.Summary> slice){
        return new AlbumApiDto.GetAllResponse(
                slice.getContent().stream().map(this::toSummaryResponse).toList(),
                slice.hasNext());
    }
}