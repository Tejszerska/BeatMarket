package com.spring.songify.infrastructure.crud.controller.song.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
@Schema(description = "Response payload after successfully assigning a genre to a song")
@Builder
public record AssignGenreToSongResponseDto(
        @Schema(description = "ID of the song", example = "10") Long songId,
        @Schema(description = "Title of the song", example = "In the End") String songTitle,
        @Schema(description = "ID of the assigned genre", example = "2") Long genreId,
        @Schema(description = "Name of the assigned genre", example = "Nu Metal") String genreName
) {
}
