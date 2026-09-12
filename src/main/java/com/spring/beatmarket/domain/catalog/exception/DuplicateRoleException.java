package com.spring.beatmarket.domain.catalog.exception;

import lombok.Getter;

import java.util.Set;

@Getter
public class DuplicateRoleException extends RoleConflictException {
    private final String targetName;
    private final Set<Long> conflictingIds;

    public DuplicateRoleException(final String subjectName, final String targetName, final Set<Long> conflictingIds) {
        super(String.format("%s cannot be both main and featured on the same %s", subjectName, targetName), subjectName);
        this.targetName = targetName;
        this.conflictingIds = conflictingIds;
    }
}