package com.jesga.url_shortener_service.exception;

import com.jesga.url_shortener_service.dto.ErrorResponse;
import com.jesga.url_shortener_service.exception.custom.ExpiredUrlException;
import com.jesga.url_shortener_service.exception.custom.URLNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {

        String errors = ex.getBindingResult()
                .getAllErrors()
                .stream().map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        errors,
                        HttpStatus.BAD_REQUEST.value(),
                        LocalDateTime.now()
                ));

    }

    @ExceptionHandler(ExpiredUrlException.class)
    public ResponseEntity<ErrorResponse> handleExpiredUrlException(ExpiredUrlException ex) {
        return ResponseEntity.status(HttpStatus.GONE).body(new ErrorResponse(
                ex.getMessage(),
                HttpStatus.GONE.value(),
                LocalDateTime.now()
        ));
    }

    @ExceptionHandler(URLNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleURLNotFoundException(URLNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(
                "Unexpected error: " + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now()
        ));
    }

}
