package br.com.matheusgusmao.incometax.infra.exception;

import br.com.matheusgusmao.incometax.infra.exception.custom.EntityAlreadyExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<ApiException> handleEntityAlreadyExists(EntityAlreadyExistsException exception) {
        return buildErrorResponse(CONFLICT, exception.getMessage(), exception.getClass().getSimpleName());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiException> handleEntityNotFound(EntityNotFoundException exception) {
        return buildErrorResponse(NOT_FOUND, exception.getMessage(), exception.getClass().getSimpleName());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiException> handleUsernameNotFound(UsernameNotFoundException exception) {
        return buildErrorResponse(NOT_FOUND, exception.getMessage(), exception.getClass().getSimpleName());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiException> handleAccessDenied(AccessDeniedException exception) {
        return buildErrorResponse(FORBIDDEN, exception.getMessage(), exception.getClass().getSimpleName());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiException> handleBadCredentials(BadCredentialsException exception) {
        return buildErrorResponse(UNAUTHORIZED, "Invalid credentials", exception.getClass().getSimpleName());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiException> handleIllegalArgument(IllegalArgumentException exception) {
        return buildErrorResponse(BAD_REQUEST, exception.getMessage(), exception.getClass().getSimpleName());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiException> handleIllegalState(IllegalStateException exception) {
        return buildErrorResponse(BAD_REQUEST, exception.getMessage(), exception.getClass().getSimpleName());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiException> handleValidationErrors(MethodArgumentNotValidException exception) {
        var errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        
        return buildErrorResponse(BAD_REQUEST, "Validation failed: " + errors, exception.getClass().getSimpleName());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiException> handleGenericException(Exception exception) {
        return buildErrorResponse(INTERNAL_SERVER_ERROR, "An unexpected error occurred", exception.getClass().getSimpleName());
    }

    private ResponseEntity<ApiException> buildErrorResponse(HttpStatus status, String message, String developerMessage) {
        var apiException = ApiException.builder()
                .status(status)
                .message(message)
                .developerMessage(developerMessage)
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(apiException, status);
    }
}