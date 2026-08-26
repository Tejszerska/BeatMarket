package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.exception.RoleConflictException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
class RoleValidator {

    /**
     * Validates that an entity cannot have both main and featured roles simultaneously.
     *
     * @param mainIds    List of IDs where the subject is the main entity.
     * @param featIds    List of IDs where the subject is a featured entity.
     * @param subjectName The name of the entity being assigned (e.g., "Artist").
     * @param targetName  The name of the entity being assigned to (e.g., "Song", "Album").
     * @return Combined list of all valid IDs.
     */
    List<Long> combineAndValidateIds(List<Long> mainIds, List<Long> featIds, String subjectName, String targetName) {
        List<Long> safeMain = mainIds != null ? mainIds : Collections.emptyList();
        List<Long> safeFeat = featIds != null ? featIds : Collections.emptyList();

        Set<Long> conflictingIds = safeMain.stream()
                .filter(safeFeat::contains)
                .collect(Collectors.toSet());

        if (!conflictingIds.isEmpty()) {
            throw new RoleConflictException(subjectName, targetName, conflictingIds);
        }

        return Stream.concat(safeMain.stream(), safeFeat.stream()).toList();
    }
}