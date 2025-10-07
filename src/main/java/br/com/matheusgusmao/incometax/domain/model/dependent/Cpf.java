package br.com.matheusgusmao.incometax.domain.model.dependent;

import java.util.Objects;

public class Cpf {
    private final String value;

    public Cpf(String value) {
        if (value == null || !value.matches("\\d{11}")) {
            throw new IllegalArgumentException("CPF inválido");
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cpf)) return false;
        Cpf cpf = (Cpf) o;
        return value.equals(cpf.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}