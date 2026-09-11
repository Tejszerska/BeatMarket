package com.spring.beatmarket.domain.catalog;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

@Component
@RequiredArgsConstructor
class ArtistRoleManager {
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

        applyAssignments(assignmentAction, mainArtistId, allArtistIds);
    }

    public void sync(final Optional<Long> mainArtistId,
                     final Optional<List<Long>> featArtistIds,
                     final String subjectEntityName,
                     final List<Artist> currentArtists,
                     final Runnable clearAction,
                     final BiConsumer<Artist, Boolean> assignmentAction) {

        List<Artist> safeCurrentArtists = currentArtists != null ? new ArrayList<>(currentArtists) : new ArrayList<>();

        Long currentMainId = safeCurrentArtists.isEmpty() ? null : safeCurrentArtists.get(0).getId();
        List<Long> currentFeatIds = safeCurrentArtists.stream()
                .skip(1)
                .map(Artist::getId)
                .toList();

        Long targetMainId = mainArtistId == null ? currentMainId : mainArtistId.orElse(null);
        List<Long> targetFeatIds = featArtistIds == null ? currentFeatIds : featArtistIds.orElse(Collections.emptyList());

        List<Long> targetMainList = targetMainId != null ? List.of(targetMainId) : null;

        Set<Long> allTargetIds = roleValidator.combineAndValidateIds(
                targetMainList, targetFeatIds, subjectEntityName, "Artist"
        );

        clearAction.run();

        applyAssignments(assignmentAction, targetMainId, allTargetIds);
    }

    private void applyAssignments(final BiConsumer<Artist, Boolean> assignmentAction, final Long targetMainId, final Set<Long> allTargetIds) {
        if (!allTargetIds.isEmpty()) {
            List<Artist> newArtists = artistRetriever.getActives(allTargetIds);
            for (Artist artist : newArtists) {
                boolean isMain = targetMainId != null && targetMainId.equals(artist.getId());
                assignmentAction.accept(artist, isMain);
            }
        }
    }
}
