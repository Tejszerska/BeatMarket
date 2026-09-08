package com.spring.beatmarket.domain.catalog;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

@Component
@RequiredArgsConstructor
class ArtistRoleAssigner {
    private final RoleValidator roleValidator;
    private final ArtistRetriever artistRetriever;

    void assign(Long mainArtistId,
                Collection<Long> featIds,
                String subjectEntityName,
                BiConsumer<Artist,Boolean> assignmentAction) {

        List<Long> mainList = mainArtistId != null ? List.of(mainArtistId) : null;

        Set<Long> allArtistIds = roleValidator.combineAndValidateIds(
                mainList, featIds, "Artist", subjectEntityName
        );

        if (!allArtistIds.isEmpty()) {
            List<Artist> artists = artistRetriever.getActives(allArtistIds);

            for (Artist artist : artists) {
                boolean isMain = mainArtistId != null && mainArtistId.equals(artist.getId());
                assignmentAction.accept(artist, isMain);
            }
        }
    }
}
