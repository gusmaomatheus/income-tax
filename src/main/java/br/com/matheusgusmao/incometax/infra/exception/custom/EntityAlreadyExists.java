package br.com.matheusgusmao.incometax.infra.exception.custom;

public class EntityAlreadyExists extends RuntimeException {
    public EntityAlreadyExists(String message) {
        super(message);
    }
}