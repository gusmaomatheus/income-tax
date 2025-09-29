package br.com.matheusgusmao.incometax.infra.exception;

import br.com.matheusgusmao.incometax.infra.exception.custom.EntityAlreadyExists;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(value = NullPointerException.class)
    public ResponseEntity<?> handleNullPointerException(NullPointerException exception) {
        final HttpStatus status = INTERNAL_SERVER_ERROR;
        final ApiException apiException = ApiException.builder()
                .status(status)
                .message(exception.getMessage())
                .developerMessage(exception.getClass().getName())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(apiException, status);
    }

    @ExceptionHandler(value = EntityAlreadyExists.class)
    public ResponseEntity<?> handleEntityAlreadyExists(EntityAlreadyExists exception) {
        final HttpStatus status = CONFLICT;
        final ApiException apiException = ApiException.builder()
                .status(status)
                .message(exception.getMessage())
                .developerMessage(exception.getClass().getName())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(apiException, status);
    }

    @ExceptionHandler(value = UsernameNotFoundException.class)
    public ResponseEntity<?> handleUsernameNotFoundException(UsernameNotFoundException exception) {
        final HttpStatus status = NOT_FOUND;
        final ApiException apiException = ApiException.builder()
                .status(status)
                .message(exception.getMessage())
                .developerMessage(exception.getClass().getName())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(apiException, status);
    }
}