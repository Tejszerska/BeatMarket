package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.util.BaseEntity;
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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Builder
@Getter
@Entity
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    @ManyToOne(fetch = FetchType.LAZY)
    private Genre genre;

    @Column(nullable = false)
    private String title;

    private Instant releaseDate;
    private Long duration;

    @Column(columnDefinition = "TEXT")
    private String previewUrl;

    @Column(columnDefinition = "TEXT")
    private String fileUrl;

    @Enumerated(EnumType.STRING)
    private SongLanguage language;

    @ManyToOne
    private Album album;

    @ManyToMany
    private Set<Artist> artists = new HashSet<>();

    public Song(String title) {
        this.title = title;
    }

    Song(final String title, final Instant releaseDate, final Long duration, final SongLanguage songLanguage, final Genre genre) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.language = songLanguage;
        this.genre = genre;
    }

    void assignDefaultTitle(){
        this.title = "Default song:" + this.uuid.toString();
    }
}