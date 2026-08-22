package com.spring.beatmarket.infrastructure.error;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.spring.beatmarket.domain.catalog.exception.DataConflictException;
import com.spring.beatmarket.domain.catalog.exception.NameIsBlankException;
import com.spring.beatmarket.domain.catalog.exception.ResourceNotFoundException;
import com.spring.beatmarket.domain.catalog.exception.TitleIsBlankException;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.song.InvalidSearchCriteriaException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
class GlobalExceptionHandler {

    @ExceptionHandler({
            ResourceNotFoundException.class
    })
    public ResponseEntity<SingleStringErrorResponseDto> handleNotFoundExceptions(RuntimeException exception) {
        SingleStringErrorResponseDto errorResponse = new SingleStringErrorResponseDto(exception.getMessage());

        log.warn("Resource not found: {}", exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }
    @ExceptionHandler({
            DataConflictException.class
    })
    public ResponseEntity<SingleStringErrorResponseDto> handleDataConflictExceptions(RuntimeException exception) {
        SingleStringErrorResponseDto errorResponse = new SingleStringErrorResponseDto(exception.getMessage());

        log.warn("Data conflict: {}", exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponseDto> handleValidationException(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult().getFieldErrors()
                .forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));

        ValidationErrorResponseDto response = new ValidationErrorResponseDto("Validation failed", errors);

        log.warn("Validation failed: {}", errors);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler({
            TitleIsBlankException.class,
            NameIsBlankException.class
    })
    public ResponseEntity<SingleStringErrorResponseDto> handleBlankException(IllegalArgumentException exception){
        SingleStringErrorResponseDto response = new SingleStringErrorResponseDto(exception.getMessage());

        log.warn("Resource can't be blank: {}", exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler({
            InvalidSearchCriteriaException.class
    })
    public ResponseEntity<ValidationErrorResponseDto> handleInvalidFiltering(InvalidSearchCriteriaException exception){
        Map<String, String> errors = new HashMap<>();
        errors.put(exception.getField(), exception.getMessage());
        ValidationErrorResponseDto errorResponseDto = new ValidationErrorResponseDto("Validation failed", errors);

        log.warn("Validation failed: {}", errors);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponseDto);
    }

    @ExceptionHandler({
            DataIntegrityViolationException.class
    })
    public ResponseEntity<SingleStringErrorResponseDto> handleDataViolation(DataIntegrityViolationException exception) {
        SingleStringErrorResponseDto errorResponse =
                new SingleStringErrorResponseDto("A resource with this unique value already exists.");

        log.warn("Data integrity violation: {} ", exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<SingleStringErrorResponseDto> handleIllegalArgumentException(IllegalArgumentException ex) {
        SingleStringErrorResponseDto errorResponse = new SingleStringErrorResponseDto(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<Object> handleInvalidJSON(HttpMessageNotReadableException exception){
        Map<String, String> errors = new HashMap<>();

        Throwable cause = exception.getCause();
        if(cause instanceof InvalidFormatException invalidFormatException){
            invalidFormatException.getPath().forEach( path -> {
                String fieldName = path.getFieldName();
                String errorMessage = String.format("Invalid value '%s'. Expected format '%s'",
                        invalidFormatException.getValue(), invalidFormatException.getTargetType().getSimpleName());
                errors.put(fieldName, errorMessage);
            });
        }
        if(errors.isEmpty()){
            SingleStringErrorResponseDto singleStringError = new SingleStringErrorResponseDto("Malformed JSON request");
            log.warn("Malformed JSON request: {}", exception.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(singleStringError);
        }

        ValidationErrorResponseDto errorResponseDto =
                new ValidationErrorResponseDto("Validation failed due to invalid data format", errors);

        log.warn("Validation failed: {}", errors);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponseDto);
    }
}
