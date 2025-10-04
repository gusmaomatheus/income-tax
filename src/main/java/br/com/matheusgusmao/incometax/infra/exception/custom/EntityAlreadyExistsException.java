package br.com.matheusgusmao.incometax.infra.exception.custom;

public class EntityAlreadyExistsException extends RuntimeException {
    public EntityAlreadyExistsException(String message) {
        super(message);
    }
}