package com.spring.songify.infrastructure.controller.crud.song.controller.song;

import com.spring.songify.domain.crud.dto.SongDto;
import com.spring.songify.domain.crud.dto.SongRequestDto;
import com.spring.songify.infrastructure.controller.crud.song.controller.song.dto.request.CreateSongRequestDto;
import com.spring.songify.infrastructure.controller.crud.song.controller.song.dto.request.PartiallyUpdateSongRequestDto;
import com.spring.songify.infrastructure.controller.crud.song.controller.song.dto.request.UpdateSongRequestDto;
import com.spring.songify.infrastructure.controller.crud.song.controller.song.dto.response.AssignGenreToSongResponseDto;
import com.spring.songify.infrastructure.controller.crud.song.controller.song.dto.response.DeleteSongResponseDto;
import com.spring.songify.infrastructure.controller.crud.song.controller.song.dto.response.GetAllSongsResponseDto;
import com.spring.songify.infrastructure.controller.crud.song.controller.song.dto.response.GetSongResponseDto;
import com.spring.songify.infrastructure.controller.crud.song.controller.song.dto.response.PartiallyUpdateSongResponseDto;
import com.spring.songify.infrastructure.controller.crud.song.controller.song.dto.response.UpdateSongResponseDto;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;

import java.util.List;

class SongControllerMapper {


    static AssignGenreToSongResponseDto mapFromSongDtoToAssignGenreToSongResponseDto(SongDto dto) {
        return new AssignGenreToSongResponseDto(dto);
    }

    static SongRequestDto mapFromSongCreateSongRequestDtoToDomainRequest(CreateSongRequestDto createSongRequestDto) {
        return SongRequestDto.builder()
                .name(createSongRequestDto.songName())
                .duration(createSongRequestDto.duration())
                .releaseDate(createSongRequestDto.releaseDate())
                .language(createSongRequestDto.language())
                .build();
    }

    static SongDto mapFromUpdateSongRequestDtoToSongDto(UpdateSongRequestDto dto) {
        return SongDto
                .builder()
                .name(dto.songName())
                .build();
    }

    static SongDto mapFromPartiallyUpdateSongRequestDtoToSong(PartiallyUpdateSongRequestDto dto) {
        return SongDto
                .builder()
                .name(dto.songName())
                .build();
    }

    static DeleteSongResponseDto mapFromSongToDeleteSongResponseDto(Long id) {
        return new DeleteSongResponseDto("You deleted song with id: " + id, HttpStatus.OK);
    }

    static UpdateSongResponseDto mapFromSongToUpdateSongResponseDto(SongDto songDto) {
        return new UpdateSongResponseDto(songDto);
    }

    static PartiallyUpdateSongResponseDto mapFromSongDtoToPartiallyUpdateSongResponseDto(SongDto songDto) {
        return new PartiallyUpdateSongResponseDto(songDto);
    }

    static GetSongResponseDto mapFromSongToGetSongResponseDto(SongDto songDto) {
        return new GetSongResponseDto(songDto);
    }

    static GetAllSongsResponseDto mapFromSongToGetAllSongsResponseDto(Slice<SongDto> slice) {
        List<SongDto> content = slice.getContent();
        boolean hasNext = slice.hasNext();
        return new GetAllSongsResponseDto(content, hasNext);
    }
}
