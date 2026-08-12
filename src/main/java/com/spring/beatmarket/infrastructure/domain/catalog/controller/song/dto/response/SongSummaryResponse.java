package com.spring.beatmarket.infrastructure.domain.catalog.controller.song.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spring.beatmarket.domain.catalog.dto.PriceWithCurrencyDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Schema(description = "Summary representation of a single song response with pricing info")
public record SongSummaryResponse(
        @Schema(description = "Song ID", example = "10") Long id,
        @Schema(description = "Song title", example = "Shadow Realm") String title,
        @Schema(description = "Artist name", example = "[\"Chillstone\", \"Neon Drift\"]") List<String> artists,
        @Schema(description = "Genre name", example = "Lo-Fi") String genre,
        @Schema(description = "Url of tracks preview", example = "https://some.link.com/audio.mp3") String previewUrl,
        @Schema(description = "Album name", example = "Stones of divinity") String album,
        @Schema(description = "Song language", example = "EN") String language,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        @Schema(description = "Release date in YYYY-MM-DD format", example = "2000-10-24") LocalDate releaseDate,
        @Schema(description = "Song duration", example = "250") Long duration,
        @Schema(description = "Song pricing tiers",
                example = "{\"Standard\": 20.00, \"Commercial\": 50.00}" ) Map<String, PriceWithCurrencyDto> pricing
) {
}
