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
import jakarta.persistence.JoinTable;
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
    private Integer duration;

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
    @JoinTable(
            name = "song_artist",
            joinColumns = @JoinColumn(name = "song_id"),
            inverseJoinColumns = @JoinColumn(name = "artist_id")
    )
    @OrderColumn(name = "artist_order")
    private List<Artist> artists = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String previewUrl;

    @Column(columnDefinition = "TEXT")
    private String fileUrl;

    Song(final String title, final LocalDate releaseDate, final Integer duration, final SongLanguage language,
         final Genre genre, final Album album, final List<Artist> artists){
        this(title, releaseDate, duration, language,
                genre, album, artists, null, null);
    }

    /**
     * Enforces strict business invariants for Song creation.
     * Blocks invalid states (e.g., negative duration, future release dates)
     * to act as the primary process guardian.
     */
    @Builder
    Song(final String title, final LocalDate releaseDate, final Integer duration, final SongLanguage language,
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
        this.artists = artists != null ? artists : new ArrayList<>();
        this.previewUrl = previewUrl;
        this.fileUrl = fileUrl;
    }

    void changeTitle(String newTitle) {
        if (newTitle == null || newTitle.isBlank()) throw new MissingRequiredFieldException("title");
        this.title = newTitle;
    }

    void changeDuration(Integer newDuration) {
        if (newDuration == null) throw new MissingRequiredFieldException("duration");
        if (newDuration <= 0) throw new IllegalArgumentException("Duration must be a positive number");
        this.duration = newDuration;
    }

    void changeLanguage(SongLanguage language) {
        if (language == null) throw new MissingRequiredFieldException("language");
        this.language = language;
    }

    /**
     * Updates the release date while ensuring compliance with the active catalog business rules.
     */
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

    /**
     * Manages bidirectional synchronization with Artist.
     * The 'contains' check prevents infinite recursion during the assignment process.
     */
    void addArtist(Artist artist) {
        if (artist != null && !this.artists.contains(artist)) {
            this.artists.add(artist);

            if (!artist.getSongs().contains(this)) {
                artist.addSong(this);
            }
        }
    }

    void removeArtist(Artist artist) {
        if (artist != null && this.artists.contains(artist)) {
            this.artists.remove(artist);

            if (artist.getSongs().contains(this)) {
                artist.removeSong(this);
            }
        }
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