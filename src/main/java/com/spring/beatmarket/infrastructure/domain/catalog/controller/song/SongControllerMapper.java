package com.spring.beatmarket.infrastructure.domain.catalog.controller.song;

import com.spring.beatmarket.domain.catalog.dto.song.CreateSongDto;
import com.spring.beatmarket.domain.catalog.dto.song.SongDetailsDto;
import com.spring.beatmarket.domain.catalog.dto.song.SongDto;
import com.spring.beatmarket.domain.catalog.dto.song.SongSearchCriteria;
import com.spring.beatmarket.domain.catalog.dto.song.SongSummaryDto;
import com.spring.beatmarket.domain.catalog.dto.song.UpdateSongDto;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.song.dto.request.CreateSongRequest;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.song.dto.request.SongSearchRequest;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.song.dto.request.UpdateSongRequest;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.song.dto.response.GetAllSongsResponse;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.song.dto.response.SongDetailsResponse;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.song.dto.response.SongResponse;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.song.dto.response.SongSummaryResponse;
import com.spring.beatmarket.infrastructure.domain.shared.JsonNullableMapper;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Slice;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = JsonNullableMapper.class)
public interface SongControllerMapper {

    CreateSongDto toDomain(CreateSongRequest createSongRequest);

    SongDetailsResponse toDomain(SongDetailsDto songDetailsDto);

    SongSearchCriteria toDomain(SongSearchRequest songSearchRequest);

    UpdateSongDto toDomain(UpdateSongRequest request);

    SongResponse toResponse(SongDto dto);

    List<SongSummaryResponse> toResponse(List<SongSummaryDto> list);

    default GetAllSongsResponse toResponse(Slice<SongSummaryDto> slice) {
        return new GetAllSongsResponse(
                toResponse(slice.getContent()),
                slice.hasNext()
        );
    }

}