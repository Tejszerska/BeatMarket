package com.spring.beatmarket.domain.catalog;

import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class ArtistRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private SongRepository songRepository;

    @BeforeEach
    void setUp() {
        entityManager.getEntityManager().createQuery("DELETE FROM Song").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM Album").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM Artist").executeUpdate();
    }

    @Test
    @DisplayName("Should save Artist and retrieve it")
    void should_save_and_retrieve_artist() {
        // given
        Artist artist = createAndSaveArtist("Eminem");
        flushAndClear();

        // when
        Optional<Artist> retrieved = artistRepository.findByIdAndActiveTrue(artist.getId());

        // then
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getName()).isEqualTo("Eminem");
    }

    @Test
    @DisplayName("Should return empty Optional when Artist is inactive")
    void should_return_empty_optional_when_inactive() {
        // given
        Artist artist = createAndSaveArtist("Eminem");
        artist.deactivate();
        flushAndClear();

        // when
        Optional<Artist> retrieved = artistRepository.findByIdAndActiveTrue(artist.getId());

        // then
        assertThat(retrieved).isEmpty();
    }

    @Test
    @DisplayName("Should return Slice with active artists only")
    void should_return_slice_with_active_artists() {
        // given
        Artist artist1 = createAndSaveArtist("Artist1");
        Artist artist2 = createAndSaveArtist("Artist2");
        Artist inactiveArtist = createAndSaveArtist("Artist3");
        inactiveArtist.deactivate();

        flushAndClear();

        // when
        Slice<Artist> artistSlice = artistRepository.findByActiveTrue(Pageable.ofSize(5));

        // then
        assertThat(artistSlice.getContent())
                .extracting(Artist::getId)
                .containsExactlyInAnyOrder(artist1.getId(), artist2.getId())
                .doesNotContain(inactiveArtist.getId());
    }

    @Test
    @DisplayName("Should return Slice filtering by name ignoring case")
    void should_return_filtered_slice() {
        // given
        Artist artist1 = createAndSaveArtist("The Beatles");
        Artist artist2 = createAndSaveArtist("The Rolling Stones");
        Artist artist3 = createAndSaveArtist("Pink Floyd");

        flushAndClear();

        // when
        Slice<Artist> artistSlice = artistRepository.findByActiveTrueAndNameContainsIgnoreCase("the", Pageable.ofSize(5));

        // then
        assertThat(artistSlice.getContent())
                .extracting(Artist::getId)
                .containsExactlyInAnyOrder(artist1.getId(), artist2.getId())
                .doesNotContain(artist3.getId());
    }

    @Test
    @DisplayName("Should return list of active artists by IDs")
    void should_return_list_by_ids() {
        // given
        Artist artist1 = createAndSaveArtist("Artist1");
        Artist artist2 = createAndSaveArtist("Artist2");
        Artist inactiveArtist = createAndSaveArtist("Artist3");
        inactiveArtist.deactivate();

        List<Long> idsToSearch = List.of(artist1.getId(), artist2.getId(), inactiveArtist.getId());
        flushAndClear();

        // when
        List<Artist> retrieved = artistRepository.findByIdInAndActiveTrue(idsToSearch);

        // then
        assertThat(retrieved)
                .extracting(Artist::getId)
                .containsExactlyInAnyOrder(artist1.getId(), artist2.getId())
                .doesNotContain(inactiveArtist.getId());
    }

    @Test
    @DisplayName("Should eagerly fetch initialized songs for an artist")
    void should_fetch_artist_with_songs() {
        // given
        Artist artist = createAndSaveArtist("Artist With Songs");

        Song song = Song.builder()
                .title("Song 1")
                .releaseDate(LocalDate.now())
                .duration(180)
                .language(SongLanguage.EN)
                .build();
        // Song jest stroną zarządzającą relacją (posiada @JoinTable), więc używamy helpera
        song.assignArtist(artist, true);
        songRepository.save(song);

        flushAndClear();

        // when
        Optional<Artist> retrieved = artistRepository.findByIdWithSongs(artist.getId());

        // then
        assertThat(retrieved).isPresent();
        assertThat(Hibernate.isInitialized(retrieved.get().getSongs())).isTrue();
        assertThat(retrieved.get().getSongs()).hasSize(1);
    }

    @Test
    @DisplayName("Should eagerly fetch initialized albums for an artist")
    void should_fetch_artist_with_albums() {
        // given
        Artist artist = createAndSaveArtist("Artist With Albums");

        Album album = Album.builder()
                .title("Album 1")
                .build();
        album.assignArtist(artist, true);
        albumRepository.save(album);

        flushAndClear();

        // when
        Optional<Artist> retrieved = artistRepository.findByIdWithAlbums(artist.getId());

        // then
        assertThat(retrieved).isPresent();
        assertThat(Hibernate.isInitialized(retrieved.get().getAlbums())).isTrue();
        assertThat(retrieved.get().getAlbums()).hasSize(1);
    }


    private Artist createAndSaveArtist(String name) {
        return artistRepository.save(Artist.builder().name(name).build());
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}