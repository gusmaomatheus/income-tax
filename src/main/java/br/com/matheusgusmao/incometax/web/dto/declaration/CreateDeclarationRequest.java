package br.com.matheusgusmao.incometax.web.dto.declaration;

import jakarta.validation.constraints.NotNull;

public record CreateDeclarationRequest(@NotNull(message = "Year cannot be null") int year) {
}