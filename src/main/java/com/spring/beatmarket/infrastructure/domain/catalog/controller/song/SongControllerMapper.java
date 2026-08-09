package com.spring.beatmarket.infrastructure.domain.catalog.controller.song;

import com.spring.beatmarket.domain.catalog.dto.SongCreatedDto;
import com.spring.beatmarket.domain.catalog.dto.SongDetailsDto;
import com.spring.beatmarket.domain.catalog.dto.SongDto;
import com.spring.beatmarket.domain.catalog.dto.SongRequestDto;
import com.spring.beatmarket.domain.catalog.dto.SongSearchCriteria;
import com.spring.beatmarket.domain.catalog.dto.SongSummaryDto;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.song.dto.request.CreateSongRequest;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.song.dto.request.SongSearchRequestDto;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.song.dto.response.AssignGenreToSongResponseDto;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.song.dto.response.CreateSongResponse;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.song.dto.response.GetAllSongsResponse;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.song.dto.response.PartiallyUpdateSongResponseDto;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.song.dto.response.SongDetailsResponse;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.song.dto.response.SongSummaryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Slice;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SongControllerMapper {

    @Mapping(source = "id", target = "songId")
    @Mapping(source = "title", target = "songTitle")
    @Mapping(source = "genre.id", target = "genreId")
    @Mapping(source = "genre.name", target = "genreName")
    AssignGenreToSongResponseDto mapFromSongDtoToAssignGenreToSongResponseDto(SongDto dto);


    SongRequestDto mapFromCreateSongRequestToDomainRequest(CreateSongRequest createSongRequest);

    // SongDto mapFromPartiallyUpdateSongRequestDtoToSong(PartiallyUpdateSongRequestDto dto);
    PartiallyUpdateSongResponseDto mapFromSongDtoToPartiallyUpdateSongResponseDto(SongDto songDto);

    SongDetailsResponse mapFromDomainToSongDetailsResponse(SongDetailsDto songDetailsDto);

    // SongResponseDto mapFromDomainSongDtoToSongResponseDto(SongDto songDto);
    CreateSongResponse mapFromSongDtoToCreateSongResponseDto(SongDto savedSong);
    List<SongSummaryResponse> mapToListOfSongResponseDto(List<SongSummaryDto> list);

    SongSearchCriteria mapFromSearchRequestToDomain(SongSearchRequestDto songSearchRequestDto);
    CreateSongResponse mapFromSongCreatedDtoToResponse(SongCreatedDto songCreatedDto);
    default GetAllSongsResponse mapFromSongToGetAllSongsResponseDto(Slice<SongSummaryDto> slice) {
        return new GetAllSongsResponse(
                mapToListOfSongResponseDto(slice.getContent()),
                slice.hasNext()
        );
    }

}