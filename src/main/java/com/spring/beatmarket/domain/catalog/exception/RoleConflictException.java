package com.spring.beatmarket.domain.catalog.exception;

public abstract class RoleConflictException extends DataConflictException {
    String subjectName;

    public RoleConflictException(String message, String subjectName) {
        super(message);
        this.subjectName = subjectName;
    }
}
