package com.spring.songify.infrastructure.controller.crud.controller.song;

import com.spring.songify.domain.crud.dto.SongDto;
import com.spring.songify.domain.crud.dto.SongRequestDto;
import com.spring.songify.infrastructure.controller.crud.controller.song.dto.request.CreateSongRequestDto;
import com.spring.songify.infrastructure.controller.crud.controller.song.dto.request.PartiallyUpdateSongRequestDto;
import com.spring.songify.infrastructure.controller.crud.controller.song.dto.request.UpdateSongRequestDto;
import com.spring.songify.infrastructure.controller.crud.controller.song.dto.response.AssignGenreToSongResponseDto;
import com.spring.songify.infrastructure.controller.crud.controller.song.dto.response.CreateSongResponseDto;
import com.spring.songify.infrastructure.controller.crud.controller.song.dto.response.GetAllSongsResponseDto;
import com.spring.songify.infrastructure.controller.crud.controller.song.dto.response.GetSongResponseDto;
import com.spring.songify.infrastructure.controller.crud.controller.song.dto.response.PartiallyUpdateSongResponseDto;
import com.spring.songify.infrastructure.controller.crud.controller.song.dto.response.SongResponseDto;
import com.spring.songify.infrastructure.controller.crud.controller.song.dto.response.UpdateSongResponseDto;
import org.springframework.data.domain.Slice;

import java.util.List;

class SongControllerMapper {


    static AssignGenreToSongResponseDto mapFromSongDtoToAssignGenreToSongResponseDto(SongDto dto) {
        return AssignGenreToSongResponseDto.builder()
                .songId(dto.id())
                .songTitle(dto.title())
                .genreId(dto.genre().id())
                .genreName(dto.genre().name())
                .build();
    }

    static SongRequestDto mapFromSongCreateSongRequestDtoToDomainRequest(CreateSongRequestDto createSongRequestDto) {
        return SongRequestDto.builder()
                .name(createSongRequestDto.title())
                .duration(createSongRequestDto.duration())
                .releaseDate(createSongRequestDto.releaseDate())
                .language(createSongRequestDto.language())
                .build();
    }

    static SongDto mapFromUpdateSongRequestDtoToSongDto(UpdateSongRequestDto dto) {
        return SongDto
                .builder()
                .title(dto.title())
                .build();
    }

    static SongDto mapFromPartiallyUpdateSongRequestDtoToSong(PartiallyUpdateSongRequestDto dto) {
        return SongDto
                .builder()
                .title(dto.title())
                .build();
    }

    static UpdateSongResponseDto mapFromSongToUpdateSongResponseDto(SongDto songDto) {
        return new UpdateSongResponseDto(songDto.id(), songDto.title());
    }

    static PartiallyUpdateSongResponseDto mapFromSongDtoToPartiallyUpdateSongResponseDto(SongDto songDto) {
        return new PartiallyUpdateSongResponseDto(songDto.id(), songDto.title());
    }

    static GetSongResponseDto mapFromSongToGetSongResponseDto(SongDto songDto) {
        return new GetSongResponseDto(songDto.id(), songDto.title());
    }

    static GetAllSongsResponseDto mapFromSongToGetAllSongsResponseDto(Slice<SongDto> slice) {
        List<SongDto> content = slice.getContent();
        List<SongResponseDto> responseContent = content.stream().map(SongControllerMapper::mapFromDomainSongDtoToSongResponseDto).toList();
        boolean hasNext = slice.hasNext();
        return new GetAllSongsResponseDto(responseContent, hasNext);
    }
    static SongResponseDto mapFromDomainSongDtoToSongResponseDto (SongDto songDto){
        return new SongResponseDto(songDto.id(), songDto.title());
    }

    static CreateSongResponseDto mapFromSongDtoToCreateSongResponseDto(final SongDto savedSong) {
        return new CreateSongResponseDto(savedSong.id(), savedSong.title());
    }
}
