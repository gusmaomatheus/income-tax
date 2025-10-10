package br.com.matheusgusmao.incometax.web.dto.declaration;

import br.com.matheusgusmao.incometax.domain.model.declaration.Declaration;

import java.time.LocalDateTime;

public record DeclarationResponse(Long id, int year, String status, LocalDateTime deliveryDate) {

    public static DeclarationResponse from(Declaration declaration) {
        return new DeclarationResponse(
                declaration.getId(),
                declaration.getYear(),
                declaration.getStatus().name(),
                declaration.getDeliveryDate()

        );
    }
}