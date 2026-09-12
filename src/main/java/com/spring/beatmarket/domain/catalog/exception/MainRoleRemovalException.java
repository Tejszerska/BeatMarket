package com.spring.beatmarket.domain.catalog.exception;

import lombok.Getter;

@Getter
public class MainRoleRemovalException extends RoleConflictException {
    private final Long artistId;
    private final Long subjectId;

    public MainRoleRemovalException(final String subjectName, final Long artistId, final Long subjectId) {
        super(String.format("Cannot remove Artist id=%s from %s id=%s because it has featured artists attached.",
                artistId, subjectName, subjectId), subjectName);
        this.artistId = artistId;
        this.subjectId = subjectId;
    }
}