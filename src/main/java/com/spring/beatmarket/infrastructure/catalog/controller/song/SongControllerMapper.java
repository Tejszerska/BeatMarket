package com.spring.beatmarket.infrastructure.catalog.controller.song;

import com.spring.beatmarket.domain.catalog.dto.SongDto;
import com.spring.beatmarket.domain.catalog.dto.SongRequestDto;
import com.spring.beatmarket.infrastructure.catalog.controller.song.dto.request.CreateSongRequestDto;
import com.spring.beatmarket.infrastructure.catalog.controller.song.dto.request.PartiallyUpdateSongRequestDto;
import com.spring.beatmarket.infrastructure.catalog.controller.song.dto.response.AssignGenreToSongResponseDto;
import com.spring.beatmarket.infrastructure.catalog.controller.song.dto.response.CreateSongResponseDto;
import com.spring.beatmarket.infrastructure.catalog.controller.song.dto.response.GetAllSongsResponseDto;
import com.spring.beatmarket.infrastructure.catalog.controller.song.dto.response.GetSongResponseDto;
import com.spring.beatmarket.infrastructure.catalog.controller.song.dto.response.PartiallyUpdateSongResponseDto;
import com.spring.beatmarket.infrastructure.catalog.controller.song.dto.response.SongResponseDto;
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

    @Mapping(source = "title", target = "name")
    SongRequestDto mapFromSongCreateSongRequestDtoToDomainRequest(CreateSongRequestDto createSongRequestDto);

    SongDto mapFromPartiallyUpdateSongRequestDtoToSong(PartiallyUpdateSongRequestDto dto);
    PartiallyUpdateSongResponseDto mapFromSongDtoToPartiallyUpdateSongResponseDto(SongDto songDto);
    @Mapping(source = "genre.id", target= "genreId")
    @Mapping(source = "genre.name", target= "genreName")
    GetSongResponseDto mapFromSongToGetSongResponseDto(SongDto songDto);
    SongResponseDto mapFromDomainSongDtoToSongResponseDto(SongDto songDto);
    CreateSongResponseDto mapFromSongDtoToCreateSongResponseDto(SongDto savedSong);

    List<SongResponseDto> mapToListOfSongResponseDto(List<SongDto> list);

    default GetAllSongsResponseDto mapFromSongToGetAllSongsResponseDto(Slice<SongDto> slice) {
        return new GetAllSongsResponseDto(
                mapToListOfSongResponseDto(slice.getContent()),
                slice.hasNext()
        );
    }
}