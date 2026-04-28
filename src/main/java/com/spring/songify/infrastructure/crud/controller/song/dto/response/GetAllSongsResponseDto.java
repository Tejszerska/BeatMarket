package com.spring.songify.infrastructure.crud.controller.song.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Paginated list of songs")
public record GetAllSongsResponseDto(
        @Schema(description = "List of songs for the current page") List<SongResponseDto> songs,
        @Schema(description = "Indicates if there is a next page of songs available", example = "true") boolean hasNext
) {
}
