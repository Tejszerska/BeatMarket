package com.spring.beatmarket.infrastructure.domain.catalog.controller.genre;

import com.spring.beatmarket.domain.catalog.GenreFacade;
import com.spring.beatmarket.domain.catalog.dto.GenreDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GenreControllerTest {

    private final GenreFacade facade = mock(GenreFacade.class);
    private final GenreControllerMapper mapper = mock(GenreControllerMapper.class);
    private final GenreController controller = new GenreController(facade, mapper);

    @Test
    @DisplayName("Creates a genre and returns the created response")
    void creates_genre() {
        // given
        GenreApiDto.Request request = new GenreApiDto.Request("Synthwave");
        GenreDto.Create domainRequest = new GenreDto.Create("Synthwave");
        GenreDto.Info created = new GenreDto.Info(4L, "Synthwave");
        GenreApiDto.InfoResponse expected = new GenreApiDto.InfoResponse(4L, "Synthwave");
        when(mapper.toDomainCreate(request)).thenReturn(domainRequest);
        when(facade.addGenre(domainRequest)).thenReturn(created);
        when(mapper.toInfoResponse(created)).thenReturn(expected);

        // when
        var response = controller.createGenre(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(expected);
        verify(facade).addGenre(domainRequest);
    }

    @Test
    @DisplayName("Returns all genres using the requested page")
    void returns_genres_page() {
        // given
        Pageable pageable = Pageable.ofSize(10);
        @SuppressWarnings("unchecked")
        Slice<GenreDto.Summary> slice = mock(Slice.class);
        GenreApiDto.GetAllResponse expected = new GenreApiDto.GetAllResponse(java.util.List.of(), false);
        when(facade.findAllGenres(pageable)).thenReturn(slice);
        when(mapper.toGetAllResponse(slice)).thenReturn(expected);

        // when
        var response = controller.getAllGenres(pageable);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
        verify(facade).findAllGenres(pageable);
    }

    @Test
    @DisplayName("Returns genre details for the requested ID")
    void returns_genre_details() {
        // given
        Long genreId = 4L;
        GenreDto.Details details = new GenreDto.Details(genreId, "Synthwave");
        GenreApiDto.DetailsResponse expected = new GenreApiDto.DetailsResponse(genreId, "Synthwave");
        when(facade.getGenreDetails(genreId)).thenReturn(details);
        when(mapper.toDetailsResponse(details)).thenReturn(expected);

        // when
        var response = controller.getGenreById(genreId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    @DisplayName("Deactivates a genre and returns no content")
    void deactivates_genre() {
        // when
        var response = controller.deleteGenreById(4L);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
        verify(facade).deactivateGenre(4L);
    }

    @Test
    @DisplayName("Transfers songs to another genre and returns the transfer summary")
    void transfers_genre_songs() {
        // given
        GenreDto.Transfer transfer = new GenreDto.Transfer(12, 4L, 5L);
        GenreApiDto.TransferResponse expected = new GenreApiDto.TransferResponse(12, 4L, 5L);
        when(facade.transferGenre(4L, 5L)).thenReturn(transfer);
        when(mapper.toTransferResponse(transfer)).thenReturn(expected);

        // when
        var response = controller.bulkUpdateByGenreId(4L, 5L);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    @DisplayName("Updates a genre name and returns the updated response")
    void updates_genre_name() {
        // given
        Long genreId = 4L;
        GenreApiDto.Request request = new GenreApiDto.Request("Synth pop");
        GenreDto.Update domainRequest = new GenreDto.Update("Synth pop");
        GenreDto.Info updated = new GenreDto.Info(genreId, "Synth pop");
        GenreApiDto.InfoResponse expected = new GenreApiDto.InfoResponse(genreId, "Synth pop");
        when(mapper.toDomainUpdate(request)).thenReturn(domainRequest);
        when(facade.updateGenre(genreId, domainRequest)).thenReturn(updated);
        when(mapper.toInfoResponse(updated)).thenReturn(expected);

        // when
        var response = controller.updateGenre(genreId, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
    }
}
