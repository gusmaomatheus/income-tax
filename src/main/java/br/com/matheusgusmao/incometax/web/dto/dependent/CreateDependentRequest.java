package br.com.matheusgusmao.incometax.web.dto.dependent;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record CreateDependentRequest(
    @NotBlank(message = "Name cannot be blank")
    String name,
    @NotBlank(message = "CPF cannot be blank")
    String cpf,
    @NotNull(message = "Birth date cannot be null")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    LocalDate birthDate) {
}