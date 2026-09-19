package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.exception.DuplicateRoleException;
import com.spring.beatmarket.domain.catalog.exception.MainRoleAbsentException;
import com.spring.beatmarket.domain.catalog.exception.MainRoleRemovalException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
class RoleValidator {

    // Enforces the core domain rule: an entity cannot act as both
    // the main and featured contributor on the same target (e.g., Artist on a Song).
    Set<Long> combineAndValidateIds(Collection<Long> mainIds, Collection<Long> featIds, String subjectName, String targetName) {
        Collection<Long> safeMain = mainIds != null ? mainIds : Collections.emptyList();
        Collection<Long> safeFeat = featIds != null ? featIds : Collections.emptyList();

        Set<Long> conflictingIds = safeMain.stream()
                .filter(safeFeat::contains)
                .collect(Collectors.toSet());

        if (!conflictingIds.isEmpty()) {
            throw new DuplicateRoleException(subjectName, targetName, conflictingIds);
        }
        return Stream.concat(safeMain.stream(), safeFeat.stream()).collect(Collectors.toSet());
    }

    // The domain model implicitly treats the first element (index 0) of the collection as the Main Artist.
    // This validation prevents deleting the main artist if featured artists (index > 0) are still assigned.
    void validateIsMainArtist(List<Artist> artistList, Artist artist, Long subjectId, String subjectName){
        if (artistList.size() > 1 && artistList.get(0).equals(artist)) {
            throw new MainRoleRemovalException(subjectName, artist.getId(), subjectId);
        }
    }

    // Ensures structural integrity: an entity cannot have featured artists assigned if it lacks a main artist.
    boolean validateHasMainArtist(List<Long> ids, Long checkedId, List<Artist> artists, String subjectName){
        boolean isMain = ids != null && ids.contains(checkedId);
        if (!isMain && artists.isEmpty()) {
            throw new MainRoleAbsentException(subjectName);
        }
        return isMain;
    }
}