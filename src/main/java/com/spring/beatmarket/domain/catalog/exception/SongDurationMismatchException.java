package com.spring.beatmarket.domain.catalog.exception;

import lombok.Getter;

@Getter
public class SongDurationMismatchException extends AudioConflictException {
    private final Integer durationInDatabase;
    public SongDurationMismatchException(final Integer durationInDatabase) {
        super("Cannot upload audio file when duration mismatches database record. ");
        this.durationInDatabase = durationInDatabase;
    }
}