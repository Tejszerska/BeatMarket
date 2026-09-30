package com.spring.beatmarket.infrastructure.error;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.spring.beatmarket.domain.catalog.exception.AudioFileTooBigException;
import com.spring.beatmarket.domain.catalog.exception.DataConflictException;
import com.spring.beatmarket.domain.catalog.exception.DuplicateRoleException;
import com.spring.beatmarket.domain.catalog.exception.SongDurationMismatchException;
import com.spring.beatmarket.domain.catalog.exception.UnreadableAudioFileException;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.song.InvalidFileFormatException;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.song.InvalidSearchCriteriaException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
class GlobalExceptionHandler {

    @Value("${spring.servlet.multipart.max-file-size}")
    String maxFileSize;

    /*
        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<SingleStringErrorResponse> handleNotFoundExceptions(RuntimeException exception) {
            SingleStringErrorResponse errorResponse = new SingleStringErrorResponse(exception.getMessage());

            log.warn("Resource not found: {}", exception.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(errorResponse);
        }
    */


    @ExceptionHandler(DataConflictException.class)
    public ResponseEntity<SingleStringErrorResponse> handleDataConflictExceptions(RuntimeException exception) {
        SingleStringErrorResponse errorResponse = new SingleStringErrorResponse(exception.getMessage());

        log.warn("Data conflict: {}", exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(errorResponse);
    }

    @ExceptionHandler(DuplicateRoleException.class)
    ResponseEntity<RoleConflictErrorResponse> handleRoleConflictException(final DuplicateRoleException ex) {
        RoleConflictErrorResponse response = new RoleConflictErrorResponse(
                ex.getMessage(),
                ex.getTargetName(),
                ex.getConflictingIds()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MessageAndMapErrorResponse> handleValidationException(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult().getFieldErrors()
                .forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));

        MessageAndMapErrorResponse response = new MessageAndMapErrorResponse("Validation failed", errors);

        log.warn("Validation failed: {}", errors);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler({
            InvalidSearchCriteriaException.class
    })
    public ResponseEntity<MessageAndMapErrorResponse> handleInvalidFiltering(InvalidSearchCriteriaException exception) {
        Map<String, String> errors = new HashMap<>();
        errors.put(exception.getField(), exception.getMessage());
        MessageAndMapErrorResponse errorResponseDto = new MessageAndMapErrorResponse("Validation failed", errors);

        log.warn("Validation failed: {}", errors);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponseDto);
    }

    @ExceptionHandler({
            DataIntegrityViolationException.class
    })
    public ResponseEntity<SingleStringErrorResponse> handleDataViolation(DataIntegrityViolationException exception) {
        SingleStringErrorResponse errorResponse =
                new SingleStringErrorResponse("A resource with this unique value already exists.");

        log.warn("Data integrity violation: {} ", exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<SingleStringErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        SingleStringErrorResponse errorResponse = new SingleStringErrorResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    ProblemDetail handleMaxSizeException(MaxUploadSizeExceededException ex) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.PAYLOAD_TOO_LARGE,
                "The uploaded file exceeds the server's maximum size limit."
        );
        problemDetail.setTitle("Payload Too Large");
        problemDetail.setProperty("maxSize", maxFileSize + "MB");
        return problemDetail;
    }

    @ExceptionHandler(UnreadableAudioFileException.class)
    public ResponseEntity<SingleStringErrorResponse> handleUnreadableAudioFileException(UnreadableAudioFileException ex) {
        SingleStringErrorResponse errorResponse = new SingleStringErrorResponse(ex.getMessage());
        log.warn(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(SongDurationMismatchException.class)
    ResponseEntity<SongDurationMismatchResponse> handleSongDurationMismatchException(final SongDurationMismatchException ex) {
        SongDurationMismatchResponse response = new SongDurationMismatchResponse(
                ex.getMessage(),
                ex.getDurationInDatabase());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(AudioFileTooBigException.class)
    ResponseEntity<AudioFileTooBigResponse> handleAudioFileTooBigException(final AudioFileTooBigException ex) {
        AudioFileTooBigResponse response = new AudioFileTooBigResponse(
                ex.getMessage(),
                ex.getFileSize(),
                ex.getMaxSize());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(InvalidFileFormatException.class)
    public ResponseEntity<SingleStringErrorResponse> handleInvalidFileFormatException(InvalidFileFormatException ex) {
        SingleStringErrorResponse errorResponse = new SingleStringErrorResponse(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<Object> handleInvalidJSON(HttpMessageNotReadableException exception) {
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
            SingleStringErrorResponse singleStringError = new SingleStringErrorResponse("Malformed JSON request");
            log.warn("Malformed JSON request: {}", exception.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(singleStringError);
        }

        MessageAndMapErrorResponse errorResponseDto =
                new MessageAndMapErrorResponse("Validation failed due to invalid data format", errors);

        log.warn("Validation failed: {}", errors);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponseDto);
    }
}
