package com.spring.beatmarket.domain.catalog.exception;

import lombok.Getter;

@Getter
public class MainRoleAbsentException extends RoleConflictException {

    public MainRoleAbsentException(final String subjectName) {
        super(String.format("Cannot update %ss featured artists when main artist isn't specified.",
                subjectName), subjectName);
    }
}