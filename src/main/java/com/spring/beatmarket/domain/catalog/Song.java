package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.exception.MissingRequiredFieldException;
import com.spring.beatmarket.domain.shared.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes =
        {
                @Index(
                        name = "idx_song_name",
                        columnList = "title"
                )
        })
class Song extends BaseEntity {

    @Id
    @GeneratedValue(generator = "song_id_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "song_id_seq",
            sequenceName = "song_id_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDate releaseDate;

    @Column(nullable = false)
    private Long duration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SongLanguage language;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id")
    private Genre genre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    private Album album;

    @ManyToMany(fetch = FetchType.LAZY)
    @OrderColumn(name = "artist_order")
    private List<Artist> artists = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String previewUrl;

    @Column(columnDefinition = "TEXT")
    private String fileUrl;

    Song(final String title, final LocalDate releaseDate, final Long duration, final SongLanguage language,
         final Genre genre, final Album album, final List<Artist> artists){
        this(title, releaseDate, duration, language,
                genre, album, artists, null, null);
    }

    @Builder
    Song(final String title, final LocalDate releaseDate, final Long duration, final SongLanguage language,
         final Genre genre, final Album album, final List<Artist> artists,
         final String previewUrl, final String fileUrl) {
        if (title == null || title.isBlank()) throw new MissingRequiredFieldException("title");
        if (releaseDate == null) throw new MissingRequiredFieldException("releaseDate");
        if (language == null) throw new MissingRequiredFieldException("language");
        if (duration == null) throw new MissingRequiredFieldException("duration");

        if (duration <= 0) throw new IllegalArgumentException("Duration must be a positive number");
        if (releaseDate.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("Release date can't be in the future");

        this.title = title;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.language = language;
        this.genre = genre;
        this.album = album;
        this.artists = artists;
        this.previewUrl = previewUrl;
        this.fileUrl = fileUrl;
    }

    void changeTitle(String newTitle) {
        if (newTitle == null || newTitle.isBlank()) throw new MissingRequiredFieldException("title");
        this.title = newTitle;
    }

    void changeDuration(Long newDuration) {
        if (newDuration == null) throw new MissingRequiredFieldException("duration");
        if (newDuration <= 0) throw new IllegalArgumentException("Duration must be a positive number");
        this.duration = newDuration;
    }

    void changeLanguage(SongLanguage language) {
        if (language == null) throw new MissingRequiredFieldException("language");
        this.language = language;
    }

    void changeReleaseDate(LocalDate releaseDate) {
        if (releaseDate == null) throw new MissingRequiredFieldException("releaseDate");
        if (releaseDate.isAfter(LocalDate.now())) throw new IllegalArgumentException("Release date can't be in the future");
        this.releaseDate = releaseDate;
    }

    void assignToAlbum(Album album) {
        this.album = album;
    }

    void assignToGenre(Genre genre) {
        this.genre = genre;
    }

    void detachFromAlbum() {
        this.album = null;
    }

    void detachFromGenre() {
        this.genre = null;
    }

    void changeArtistList(List<Artist> newArtists) {
        this.artists.clear();
        if (newArtists != null) {
            this.artists.addAll(newArtists);
        }
    }

    void clearArtists() {
        this.artists.clear();
    }

    void changePreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    void changeFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    void assignDefaultTitle() {
        this.title = "Default song:" + this.uuid.toString();
    }
}