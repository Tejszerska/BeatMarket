package com.spring.beatmarket.domain.catalog.exception;

import lombok.Getter;

import java.util.Set;

@Getter
public class RoleConflictException extends RuntimeException {
    private final String subjectName;
    private final String targetName;
    private final Set<Long> conflictingIds;

    public RoleConflictException(final String subjectName, final String targetName, final Set<Long> conflictingIds) {
        super(String.format("%s cannot be both main and featured on the same %s", subjectName, targetName));
        this.subjectName = subjectName;
        this.targetName = targetName;
        this.conflictingIds = conflictingIds;
    }
}