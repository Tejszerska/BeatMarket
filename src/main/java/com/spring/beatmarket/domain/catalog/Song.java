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
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Setter
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

    private LocalDate releaseDate;
    private Long duration;

    @Enumerated(EnumType.STRING)
    private SongLanguage language;

    @ManyToOne(fetch = FetchType.LAZY)
    private Genre genre;

    @ManyToOne
    private Album album;

    @ManyToMany
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


    void assignDefaultTitle() {
        this.title = "Default song:" + this.uuid.toString();
    }
}