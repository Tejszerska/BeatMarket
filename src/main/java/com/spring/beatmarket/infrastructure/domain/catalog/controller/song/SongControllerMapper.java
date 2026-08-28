package com.spring.beatmarket.infrastructure.domain.catalog.controller.song;

import com.spring.beatmarket.domain.catalog.dto.SongDto;
import com.spring.beatmarket.infrastructure.domain.shared.JsonNullableMapper;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Slice;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = JsonNullableMapper.class)
public interface SongControllerMapper {

    SongApiDto.DetailsResponse toGetAllResponse(SongDto.Details songDetailsDto);

    SongApiDto.InfoResponse toInfoResponse(SongDto.Info dto);

    List<SongApiDto.SummaryResponse> toSummaryResponse(List<SongDto.Summary> list);

    SongDto.SearchCriteria toDomainSearchCriteria(SongApiDto.SearchRequest songSearchRequest);

    SongDto.Update toDomainUpdate(SongApiDto.UpdateRequest request);

    SongDto.Create toDomainCreate(SongApiDto.CreateRequest createSongRequest);

    default SongApiDto.GetAllResponse toGetAllResponse(Slice<SongDto.Summary> slice) {
        return new SongApiDto.GetAllResponse(
                toSummaryResponse(slice.getContent()),
                slice.hasNext()
        );
    }
}
