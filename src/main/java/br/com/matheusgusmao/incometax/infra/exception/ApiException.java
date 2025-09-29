package br.com.matheusgusmao.incometax.infra.exception;

import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Builder
public class ApiException {
    private final String message;
    private final HttpStatus status;
    private final LocalDateTime timestamp;
    private final String developerMessage;
}