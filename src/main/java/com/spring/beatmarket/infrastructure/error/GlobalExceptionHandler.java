package com.spring.beatmarket.infrastructure.error;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.spring.beatmarket.domain.account.UserExistsException;
import com.spring.beatmarket.domain.account.UserNotFoundException;
import com.spring.beatmarket.domain.catalog.exception.AudioFileTooBigException;
import com.spring.beatmarket.domain.catalog.exception.DataConflictException;
import com.spring.beatmarket.domain.catalog.exception.DuplicateRoleException;
import com.spring.beatmarket.domain.catalog.exception.ImageFileTooBigException;
import com.spring.beatmarket.domain.catalog.exception.InvalidImageException;
import com.spring.beatmarket.domain.catalog.exception.ResourceNotFoundException;
import com.spring.beatmarket.domain.catalog.exception.SongDurationMismatchException;
import com.spring.beatmarket.domain.catalog.exception.UnreadableAudioFileException;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.song.InvalidSearchCriteriaException;
import com.spring.beatmarket.infrastructure.upload.InvalidUploadFileException;
import com.spring.beatmarket.infrastructure.upload.UploadFileTooBigException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @Value("${spring.servlet.multipart.max-file-size}")
    String maxFileSize;

    @ExceptionHandler(UsernameNotFoundException.class)
    ProblemDetail handleUsernameNotFoundException(UsernameNotFoundException exception) {
        log.warn("Username not found: {}", exception.getMessage());
        // Returns a generic message to avoid revealing sensitive information.
        return problem(HttpStatus.UNAUTHORIZED, "Authentication failed", "Invalid credentials.");
    }

    @ExceptionHandler(AuthenticationException.class)
    ProblemDetail handleAuthenticationException(AuthenticationException exception) {
        log.warn("Authentication failed: {}", exception.getMessage());
        // Returns a generic message to avoid revealing sensitive information.
        return problem(HttpStatus.UNAUTHORIZED, "Authentication failed", "Invalid email or password.");
    }

    @ExceptionHandler(UserExistsException.class)
    ProblemDetail handleUserExistsException(UserExistsException exception) {
        log.warn("Registration conflict: {}", exception.getMessage());
        return problem(
                HttpStatus.CONFLICT,
                "Registration conflict",
                "Registration could not be completed with the provided details."
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    ProblemDetail handleUserNotFoundException(UserNotFoundException exception) {
        log.warn("Confirmation failed: {}", exception.getMessage());
        return problem(HttpStatus.NOT_FOUND, "Confirmation failed.", "Invalid or expired confirmation token.");
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    ProblemDetail handleNotFoundExceptions(ResourceNotFoundException exception) {
        log.warn("Resource not found: {}", exception.getMessage());
        return problem(HttpStatus.NOT_FOUND, "Resource not found", exception.getMessage());
    }

    @ExceptionHandler(DataConflictException.class)
    ProblemDetail handleDataConflictExceptions(DataConflictException exception) {
        log.warn("Data conflict: {}", exception.getMessage());
        return problem(HttpStatus.CONFLICT, "Data conflict", exception.getMessage());
    }

    @ExceptionHandler(DuplicateRoleException.class)
    ProblemDetail handleRoleConflictException(final DuplicateRoleException ex) {
        ProblemDetail detail = problem(HttpStatus.CONFLICT, "Role conflict", ex.getMessage());
        detail.setProperty("entity", ex.getTargetName());
        detail.setProperty("conflictingIds", ex.getConflictingIds());
        return detail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail handleValidationException(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult().getFieldErrors()
                .forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));

        log.warn("Validation failed: {}", errors);
        ProblemDetail detail = problem(HttpStatus.BAD_REQUEST, "Validation failed", "Request validation failed.");
        detail.setProperty("errors", errors);
        return detail;
    }

    @ExceptionHandler(InvalidSearchCriteriaException.class)
    ProblemDetail handleInvalidFiltering(InvalidSearchCriteriaException exception) {
        Map<String, String> errors = new HashMap<>();
        errors.put(exception.getField(), exception.getMessage());

        log.warn("Validation failed: {}", errors);
        ProblemDetail detail = problem(HttpStatus.BAD_REQUEST, "Validation failed", exception.getMessage());
        detail.setProperty("errors", errors);
        return detail;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ProblemDetail handleDataViolation(DataIntegrityViolationException exception) {
        log.warn("Data integrity violation: {} ", exception.getMessage());
        return problem(HttpStatus.CONFLICT, "Data conflict", "A resource with this unique value already exists.");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex) {
        return problem(HttpStatus.BAD_REQUEST, "Invalid request", ex.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    ProblemDetail handleMaxSizeException(MaxUploadSizeExceededException ex) {
        ProblemDetail detail = problem(
                HttpStatus.PAYLOAD_TOO_LARGE,
                "Payload too large",
                "The uploaded file exceeds the server's maximum size limit."
        );
        detail.setProperty("maxSize", maxFileSize);
        return detail;
    }

    @ExceptionHandler(ImageFileTooBigException.class)
    ProblemDetail handleImageTooBigException(ImageFileTooBigException exception) {
        ProblemDetail detail = problem(
                HttpStatus.PAYLOAD_TOO_LARGE,
                "Image too large",
                exception.getMessage()
        );
        detail.setProperty("fileSize", exception.getFileSize());
        detail.setProperty("maxSize", exception.getMaxSize());
        return detail;
    }

    @ExceptionHandler(InvalidImageException.class)
    ProblemDetail handleInvalidImageException(InvalidImageException exception) {
        log.warn("Invalid image: {}", exception.getMessage());
        return problem(HttpStatus.BAD_REQUEST, "Invalid image", exception.getMessage());
    }

    @ExceptionHandler(UnreadableAudioFileException.class)
    ProblemDetail handleUnreadableAudioFileException(UnreadableAudioFileException ex) {
        log.warn(ex.getMessage());
        return problem(HttpStatus.BAD_REQUEST, "Unreadable audio file", ex.getMessage());
    }

    @ExceptionHandler(SongDurationMismatchException.class)
    ProblemDetail handleSongDurationMismatchException(final SongDurationMismatchException ex) {
        ProblemDetail detail = problem(HttpStatus.BAD_REQUEST, "Audio duration mismatch", ex.getMessage());
        detail.setProperty("durationInDatabase", ex.getDurationInDatabase());
        return detail;
    }

    @ExceptionHandler(AudioFileTooBigException.class)
    ProblemDetail handleAudioFileTooBigException(final AudioFileTooBigException ex) {
        ProblemDetail detail = problem(HttpStatus.PAYLOAD_TOO_LARGE, "Audio file too large", ex.getMessage());
        detail.setProperty("fileSize", ex.getFileSize());
        detail.setProperty("maxSize", ex.getMaxSize());
        return detail;
    }

    @ExceptionHandler(InvalidUploadFileException.class)
    ProblemDetail handleInvalidUploadFileException(InvalidUploadFileException ex) {
        log.warn("Invalid upload file: {}", ex.getMessage());
        return problem(HttpStatus.BAD_REQUEST, "Invalid upload file", ex.getMessage());
    }

    @ExceptionHandler(UploadFileTooBigException.class)
    ProblemDetail handleUploadFileTooBigException(UploadFileTooBigException ex) {
        ProblemDetail detail = problem(HttpStatus.PAYLOAD_TOO_LARGE, "Upload file too large", ex.getMessage());
        detail.setProperty("fileSize", ex.getFileSize());
        detail.setProperty("maxSize", ex.getMaxSize());
        detail.setProperty("sizeUnit", "bytes");
        return detail;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ProblemDetail handleInvalidJSON(HttpMessageNotReadableException exception) {
        Map<String, String> errors = new HashMap<>();

        Throwable cause = exception.getCause();
        if (cause instanceof InvalidFormatException invalidFormatException) {
            invalidFormatException.getPath().forEach(path -> {
                String fieldName = path.getFieldName();
                String errorMessage = String.format("Invalid value '%s'. Expected format '%s'",
                        invalidFormatException.getValue(), invalidFormatException.getTargetType().getSimpleName());
                errors.put(fieldName, errorMessage);
            });
        }
        if (errors.isEmpty()) {
            log.warn("Malformed JSON request: {}", exception.getMessage());
            return problem(HttpStatus.BAD_REQUEST, "Malformed request", "Malformed JSON request.");
        }

        log.warn("Validation failed: {}", errors);
        ProblemDetail detail = problem(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                "Validation failed due to invalid data format."
        );
        detail.setProperty("errors", errors);
        return detail;
    }

    private ProblemDetail problem(HttpStatus status, String title, String detail) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        return problemDetail;
    }
}
