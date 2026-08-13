package com.spring.beatmarket.infrastructure.domain.catalog.controller.song;

import com.spring.beatmarket.domain.catalog.dto.SongDetailsDto;
import com.spring.beatmarket.domain.catalog.dto.SongDto;
import com.spring.beatmarket.domain.catalog.dto.SongRequestDto;
import com.spring.beatmarket.domain.catalog.dto.SongSearchCriteria;
import com.spring.beatmarket.domain.catalog.dto.SongSummaryDto;
import com.spring.beatmarket.domain.catalog.dto.UpdateSongDto;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.song.dto.request.CreateSongRequest;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.song.dto.request.SongSearchRequestDto;
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

    SongRequestDto mapFromCreateSongRequestToDomainRequest(CreateSongRequest createSongRequest);

    SongDetailsResponse mapFromDomainToSongDetailsResponse(SongDetailsDto songDetailsDto);

    SongResponse mapFromDomainToResponse(SongDto dto);

    List<SongSummaryResponse> mapToListOfSongResponseDto(List<SongSummaryDto> list);

    SongSearchCriteria mapFromSearchRequestToDomain(SongSearchRequestDto songSearchRequestDto);


    UpdateSongDto mapUpdateRequestToDto(UpdateSongRequest request);

    default GetAllSongsResponse mapFromSongToGetAllSongsResponseDto(Slice<SongSummaryDto> slice) {
        return new GetAllSongsResponse(
                mapToListOfSongResponseDto(slice.getContent()),
                slice.hasNext()
        );
    }

}