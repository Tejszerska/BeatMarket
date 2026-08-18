package com.spring.beatmarket.infrastructure.domain.catalog.controller.song;

import com.spring.beatmarket.domain.catalog.dto.SongDto;
import com.spring.beatmarket.infrastructure.domain.shared.JsonNullableMapper;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Slice;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = JsonNullableMapper.class)
public interface SongControllerMapper {

    SongApiDto.CreateRequest toResponse(SongDto.Create createSongRequest);

    SongApiDto.DetailsResponse toResponse(SongDto.Details songDetailsDto);

    SongApiDto.InfoResponse toResponse(SongDto.Info dto);

    List<SongApiDto.SummaryResponse> toResponse(List<SongDto.Summary> list);

    SongDto.SearchCriteria toDomain(SongApiDto.SearchRequest songSearchRequest);

    SongDto.Update toDomain(SongApiDto.UpdateRequest request);

    SongDto.Create toDomain(SongApiDto.CreateRequest createSongRequest);

    default SongApiDto.GetAllResponse toResponse(Slice<SongDto.Summary> slice) {
        return new SongApiDto.GetAllResponse(
                toResponse(slice.getContent()),
                slice.hasNext()
        );
    }


}
