package br.com.matheusgusmao.incometax.domain.model.dependent;

import java.util.Objects;

public class Cpf {
    private final String value;

    public Cpf(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("CPF cannot be null or empty");
        }
        
        var cleanCpf = value.replaceAll("\\D", "");
        
        if (!cleanCpf.matches("\\d{11}")) {
            throw new IllegalArgumentException("CPF must contain exactly 11 digits");
        }
        
        if (isAllSameDigits(cleanCpf)) {
            throw new IllegalArgumentException("CPF cannot have all same digits");
        }
        
        if (!isValidCpf(cleanCpf)) {
            throw new IllegalArgumentException("Invalid CPF");
        }
        
        this.value = cleanCpf;
    }

    private boolean isAllSameDigits(String cpf) {
        return cpf.chars().distinct().count() == 1;
    }

    private boolean isValidCpf(String cpf) {
        var digits = cpf.substring(0, 9);
        var firstDigit = calculateDigit(digits, 10);
        var secondDigit = calculateDigit(digits + firstDigit, 11);
        
        return cpf.equals(digits + firstDigit + secondDigit);
    }

    private int calculateDigit(String digits, int multiplier) {
        var sum = 0;
        for (var i = 0; i < digits.length(); i++) {
            sum += Character.getNumericValue(digits.charAt(i)) * (multiplier - i);
        }
        var remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
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