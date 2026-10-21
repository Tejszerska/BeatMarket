package com.spring.beatmarket.infrastructure.error;

import com.spring.beatmarket.domain.catalog.exception.DuplicateRoleException;
import com.spring.beatmarket.domain.catalog.exception.ResourceNotFoundException;
import com.spring.beatmarket.infrastructure.domain.catalog.controller.song.InvalidSearchCriteriaException;
import com.spring.beatmarket.infrastructure.upload.InvalidUploadFileException;
import com.spring.beatmarket.infrastructure.upload.UploadFileTooBigException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(handler, "maxFileSize", "165MB");
    }

    @Test
    @DisplayName("Returns a generic unauthorized problem detail for authentication failure")
    void returns_generic_unauthorized_problem_detail() {
        ProblemDetail problem = handler.handleAuthenticationException(
                new BadCredentialsException("Account is not activated.")
        );

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(problem.getTitle()).isEqualTo("Authentication failed");
        assertThat(problem.getDetail()).isEqualTo("Invalid email or password.");
    }

    @Test
    @DisplayName("Returns a not-found problem detail")
    void returns_not_found_problem_detail() {
        ProblemDetail problem = handler.handleNotFoundExceptions(
                new ResourceNotFoundException("Song", 42L)
        );

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(problem.getTitle()).isEqualTo("Resource not found");
        assertThat(problem.getDetail()).contains("Song with id 42");
    }

    @Test
    @DisplayName("Preserves role conflict data in problem extensions")
    void preserves_role_conflict_data_in_problem_extensions() {
        ProblemDetail problem = handler.handleRoleConflictException(
                new DuplicateRoleException("Artist", "Song", Set.of(2L, 7L))
        );

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(problem.getProperties())
                .containsEntry("entity", "Song")
                .containsEntry("conflictingIds", Set.of(2L, 7L));
    }

    @Test
    @DisplayName("Preserves invalid search criteria in a problem extension")
    void preserves_invalid_search_criteria_in_problem_extension() {
        ProblemDetail problem = handler.handleInvalidFiltering(
                new InvalidSearchCriteriaException("maxPrice", "Currency is required.")
        );

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(problem.getProperties())
                .containsEntry("errors", Map.of("maxPrice", "Currency is required."));
    }

    @Test
    @DisplayName("Preserves bean validation failure in a problem extension")
    void preserves_bean_validation_failure_in_problem_extension() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors())
                .thenReturn(List.of(new FieldError("request", "title", "must not be blank")));

        ProblemDetail problem = handler.handleValidationException(exception);

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(problem.getProperties())
                .containsEntry("errors", Map.of("title", "must not be blank"));
    }

    @Test
    @DisplayName("Preserves the upload limit in a problem extension")
    void preserves_upload_limit_in_problem_extension() {
        ProblemDetail problem = handler.handleMaxSizeException(
                new MaxUploadSizeExceededException(165L * 1024 * 1024)
        );

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.PAYLOAD_TOO_LARGE.value());
        assertThat(problem.getProperties()).containsEntry("maxSize", "165MB");
    }

    @Test
    @DisplayName("Maps an invalid upload file to a bad-request problem detail")
    void maps_invalid_upload_file_to_bad_request_problem_detail() {
        ProblemDetail problem = handler.handleInvalidUploadFileException(
                new InvalidUploadFileException("Unsupported format.")
        );

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(problem.getTitle()).isEqualTo("Invalid upload file");
        assertThat(problem.getDetail()).isEqualTo("Unsupported format.");
    }

    @Test
    @DisplayName("Preserves upload size details in a payload-too-large problem")
    void preserves_upload_size_details_in_payload_too_large_problem() {
        ProblemDetail problem = handler.handleUploadFileTooBigException(
                new UploadFileTooBigException(11, 10)
        );

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.PAYLOAD_TOO_LARGE.value());
        assertThat(problem.getProperties())
                .containsEntry("fileSize", 11L)
                .containsEntry("maxSize", 10L)
                .containsEntry("sizeUnit", "bytes");
    }

    @Test
    @DisplayName("Returns a malformed-request problem detail")
    void returns_malformed_request_problem_detail() {
        HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);
        when(exception.getMessage()).thenReturn("Invalid JSON");

        ProblemDetail problem = handler.handleInvalidJSON(exception);

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(problem.getTitle()).isEqualTo("Malformed request");
        assertThat(problem.getDetail()).isEqualTo("Malformed JSON request.");
    }
}
