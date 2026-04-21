package com.spring.songify.infrastructure.crud.controller.song.dto.response;


public record GetSongResponseDto(Long id, String title, Long genreId, String genreName) {
}
