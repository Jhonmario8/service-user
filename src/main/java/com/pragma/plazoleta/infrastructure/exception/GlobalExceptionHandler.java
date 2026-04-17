package com.pragma.plazoleta.infrastructure.exception;

import com.pragma.plazoleta.domain.exception.DomainException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InfrastructureException.class)
    public ResponseEntity<ErrorResponse> handleInfrastructureException(InfrastructureException ex) {
        return ResponseEntity.status(ex.getHttpStatus())
                .body(new ErrorResponse(ex.getMessage(), ex.getHttpStatus().value()));
    }
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(DomainException ex) {
        return ResponseEntity.status(ex.getHttpStatus())
                .body(new ErrorResponse(ex.getMessage(), ex.getHttpStatus().value()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(),
                    error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(new ErrorResponse("Datos invalidos", 400, errors));

    }
}
