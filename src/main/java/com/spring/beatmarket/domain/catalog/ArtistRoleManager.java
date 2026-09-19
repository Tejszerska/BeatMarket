package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.exception.MainRoleAbsentException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

@Component
@RequiredArgsConstructor
class ArtistRoleManager {
    private final RoleValidator roleValidator;
    private final ArtistRetriever artistRetriever;


    /**
     * Delegates role assignment to a specific entity (Song/Album) using a BiConsumer action.
     * This decouples the role manager from knowing the exact domain entity being modified.
     */
    void assign(Long mainArtistId,
                Collection<Long> featArtistIds,
                String subjectEntityName,
                BiConsumer<Artist,Boolean> assignmentAction) {

        List<Long> mainList = mainArtistId != null ? List.of(mainArtistId) : null;

        Set<Long> allArtistIds = roleValidator.combineAndValidateIds(
                mainList, featArtistIds, "Artist", subjectEntityName
        );

        applyAssignments(assignmentAction, mainArtistId, allArtistIds);
    }

    /**
     * Handles PATCH-style updates where parameters wrapped in Optional dictate the intent:
     * - null: retain current state
     * - Optional.empty() or empty collection: clear existing relations
     * <p>
     * Enforces the business rule that a subject entity cannot have featured artists
     * without a designated main artist.
     */
    void sync(final Optional<Long> mainArtistId,
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

        if(targetMainId == null && !targetFeatIds.isEmpty()){
         throw new MainRoleAbsentException(subjectEntityName);
        }

        Set<Long> allTargetIds = roleValidator.combineAndValidateIds(
                targetMainList, targetFeatIds, subjectEntityName, "Artist"
        );

        clearAction.run();

        applyAssignments(assignmentAction, targetMainId, allTargetIds);
    }

    private void applyAssignments(final BiConsumer<Artist, Boolean> assignmentAction, final Long mainArtistId, final Set<Long> allArtistsIds) {
        if (!allArtistsIds.isEmpty()) {
            List<Artist> newArtists = artistRetriever.getActives(allArtistsIds);
            for (Artist artist : newArtists) {
                boolean isMain = Objects.equals(mainArtistId, artist.getId());
                assignmentAction.accept(artist, isMain);
            }
        }
    }


}
