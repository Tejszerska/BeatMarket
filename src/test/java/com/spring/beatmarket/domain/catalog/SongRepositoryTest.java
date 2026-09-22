package com.spring.beatmarket.domain.catalog;

import org.hibernate.Hibernate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SongRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private SongRepository songRepository;

    @Test
    @DisplayName("Should save Song and retrieve it")
    void should_save_and_retrieve_song() {
        // given
        Song savedSong = persister.createAndSaveSong("Song1", null);
        flushAndClear();

        // when
        Optional<Song> retrieved = songRepository.findByIdAndActiveTrue(savedSong.getId());

        // then
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getTitle()).isEqualTo("Song1");
    }

    @Test
    @DisplayName("Should return empty Optional when Song is inactive")
    void should_return_empty_optional_when_inactive() {
        // given
        Song savedSong = persister.createAndSaveSong("Song1", null);
        savedSong.deactivate();
        flushAndClear();

        // when
        Optional<Song> retrieved = songRepository.findByIdAndActiveTrue(savedSong.getId());

        // then
        assertThat(retrieved).isEmpty();
    }

    @Test
    @DisplayName("Should correctly check if exists by genre ID")
    void should_check_if_exists_by_genre_id() {
        // given
        Genre genre = persister.createAndSaveGenre("Pop");
        Song song = persister.createAndSaveSong("Song1", null);
        song.assignToGenre(genre);
        songRepository.save(song);

        flushAndClear();

        // when
        boolean existsForPop = songRepository.existsByGenreId(genre.getId());
        boolean existsForFakeId = songRepository.existsByGenreId(999L);

        // then
        assertThat(existsForPop).isTrue();
        assertThat(existsForFakeId).isFalse();
    }

    @Test
    @DisplayName("Should return set of active songs by IDs")
    void should_return_set_of_active_songs_by_ids() {
        // given
        Song song1 = persister.createAndSaveSong("Song1", null);
        Song song2 = persister.createAndSaveSong("Song2", null);
        Song inactiveSong = persister.createAndSaveSong("Song3", null);
        inactiveSong.deactivate();

        flushAndClear();

        List<Long> ids = List.of(song1.getId(), song2.getId(), inactiveSong.getId());

        // when
        Set<Song> retrievedSongs = songRepository.findByIdIsInAndActiveTrue(ids);

        // then
        assertThat(retrievedSongs)
                .extracting(Song::getId)
                .containsExactlyInAnyOrder(song1.getId(), song2.getId())
                .doesNotContain(inactiveSong.getId());
    }

    @Test
    @DisplayName("Should fetch active song with genre, artists, and album eagerly")
    void should_fetch_song_with_all_relations_eagerly() {
        // given
        Genre genre = persister.createAndSaveGenre("Rock");
        Album album = persister.createAndSaveAlbum("Album Title");
        Artist activeArtist = persister.createAndSaveArtist("Artist 1");
        Artist inactiveArtist = persister.createAndSaveArtist("Artist 2");
        inactiveArtist.deactivate();

        Song song = persister.createAndSaveSong("Song With Relations", album);
        song.assignToGenre(genre);
        song.assignArtist(activeArtist, true);
        song.assignArtist(inactiveArtist, false);
        songRepository.save(song);

        flushAndClear();

        // when
        Optional<Song> retrieved = songRepository.findSongByIdEagerly(song.getId());

        // then
        assertThat(retrieved).isPresent();
        Song retrievedSong = retrieved.get();

        assertThat(Hibernate.isInitialized(retrievedSong.getGenre())).isTrue();
        assertThat(Hibernate.isInitialized(retrievedSong.getAlbum())).isTrue();
        assertThat(Hibernate.isInitialized(retrievedSong.getArtists())).isTrue();

        assertThat(retrievedSong.getArtists()).hasSize(2);
    }

    @Test
    @DisplayName("Should return list of active songs with artists by IDs")
    void should_return_active_songs_with_artists_by_ids() {
        // given
        Artist artist = persister.createAndSaveArtist("Artist 1");

        Song song1 = persister.createAndSaveSong("Song1", null);
        song1.assignArtist(artist, true);
        songRepository.save(song1);

        Song song2 = persister.createAndSaveSong("Song2", null);
        song2.assignArtist(artist, true);
        songRepository.save(song2);

        flushAndClear();

        // when
        List<Song> results = songRepository.findActiveWithArtistsByIds(List.of(song1.getId(), song2.getId()));

        // then
        assertThat(results)
                .extracting(Song::getId)
                .containsExactlyInAnyOrder(song1.getId(), song2.getId());

        assertThat(results).allSatisfy(s ->
                assertThat(Hibernate.isInitialized(s.getArtists())).isTrue()
        );
    }

    @Test
    @DisplayName("Should bulk update genre setting version and editedOn correctly for active songs")
    void should_bulk_update_genre() {
        // given
        Genre oldGenre = persister.createAndSaveGenre("Old Genre");
        Genre newGenre = persister.createAndSaveGenre("New Genre");

        Song song1 = Song.builder()
                .title("Song1")
                .releaseDate(LocalDate.now())
                .duration(200)
                .language(SongLanguage.EN)
                .build();
        song1.assignToGenre(oldGenre);
        songRepository.save(song1);

        Song song2 = persister.createAndSaveSong("Song2", null);
        song2.assignToGenre(oldGenre);
        songRepository.save(song2);

        Song inactiveSong = persister.createAndSaveSong("Song3", null);
        inactiveSong.assignToGenre(oldGenre);
        inactiveSong.deactivate();
        songRepository.save(inactiveSong);

        flushAndClear();

        Instant now = Instant.now().truncatedTo(ChronoUnit.MICROS);

        // when
        Integer updatedCount = songRepository.bulkUpdateGenre(oldGenre.getId(), newGenre.getId(), now);

        // then
        assertThat(updatedCount).isEqualTo(2);

        Song updatedSong1 = entityManager.find(Song.class, song1.getId());
        Song notUpdatedInactive = entityManager.find(Song.class, inactiveSong.getId());

        assertThat(updatedSong1.getGenre().getId()).isEqualTo(newGenre.getId());
        assertThat(updatedSong1.getVersion()).isEqualTo(1L);
        assertThat(updatedSong1.getEditedOn()).isEqualTo(now);

        assertThat(notUpdatedInactive.getGenre().getId()).isEqualTo(oldGenre.getId());
    }

    @Test
    @DisplayName("Should deactivate songs in bulk setting version and editedOn correctly")
    void should_deactivate_in_bulk() {
        // given
        Song song1 = persister.createAndSaveSong("Song1", null);
        Song song2 = persister.createAndSaveSong("Song2", null);

        flushAndClear();

        Instant now = Instant.now().truncatedTo(ChronoUnit.MICROS);

        // when
        songRepository.deactivateAllByIds(Set.of(song1.getId(), song2.getId()), now);

        // then
        assertThat(songRepository.findByIdAndActiveTrue(song1.getId())).isEmpty();
        assertThat(songRepository.findByIdAndActiveTrue(song2.getId())).isEmpty();

        Song updatedSong1 = entityManager.find(Song.class, song1.getId());
        Song updatedSong2 = entityManager.find(Song.class, song2.getId());

        assertThat(updatedSong1.isActive()).isFalse();
        assertThat(updatedSong1.getVersion()).isEqualTo(1L);
        assertThat(updatedSong1.getEditedOn()).isEqualTo(now);

        assertThat(updatedSong2.isActive()).isFalse();
        assertThat(updatedSong2.getVersion()).isEqualTo(1L);
        assertThat(updatedSong2.getEditedOn()).isEqualTo(now);
    }
}