package com.spring.songify.infrastructure.error;

import com.spring.songify.domain.crud.exception.AlbumNotFoundException;
import com.spring.songify.domain.crud.exception.ArtistNotFoundException;
import com.spring.songify.domain.crud.exception.GenreNotfoundException;
import com.spring.songify.domain.crud.exception.SongNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
class GlobalExceptionHandler {

    private List<String> getErrorsFromException(MethodArgumentNotValidException exception){
        return exception.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
    }
    @ExceptionHandler({
            AlbumNotFoundException.class,
            ArtistNotFoundException.class,
            GenreNotfoundException.class,
            SongNotFoundException.class
    })
    public ResponseEntity<SingleStringErrorResponseDto> handleNotFoundExceptions(RuntimeException exception) {
        log.warn("Resource not found: {}", exception.getMessage());

        SingleStringErrorResponseDto errorResponse = new SingleStringErrorResponseDto(exception.getMessage(), HttpStatus.NOT_FOUND);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ListOfStringsErrorResponseDto> handleValidationException(MethodArgumentNotValidException exception) {
        List<String> errorsFromException = getErrorsFromException(exception);
        ListOfStringsErrorResponseDto response = new ListOfStringsErrorResponseDto(errorsFromException, HttpStatus.BAD_REQUEST);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }
}
