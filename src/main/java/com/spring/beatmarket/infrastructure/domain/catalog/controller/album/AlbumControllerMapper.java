package com.spring.beatmarket.infrastructure.domain.catalog.controller.album;

import com.spring.beatmarket.domain.catalog.dto.AlbumDto;
import com.spring.beatmarket.infrastructure.domain.shared.JsonNullableMapper;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Slice;

@Mapper(componentModel = "spring",
        uses = JsonNullableMapper .class)
public interface AlbumControllerMapper {
    AlbumApiDto.SummaryResponse toSummaryResponse(AlbumDto.Summary dto);

    AlbumApiDto.DetailsResponse toDetailsResponse(AlbumDto.Details albumDetails);

    AlbumDto.Create toDomainCreate (AlbumApiDto.CreateRequest createRequest);
    
    default AlbumApiDto.GetAllResponse toGetAllResponse(Slice<AlbumDto.Summary> slice) {
        return new AlbumApiDto.GetAllResponse(
                slice.getContent().stream().map(this::toSummaryResponse).toList(),
                slice.hasNext());
    }


    AlbumApiDto.InfoResponse toInfoResponse(AlbumDto.Info addedAlbum);

    AlbumDto.Update toDomainUpdate(AlbumApiDto.UpdateRequest request);
}