package br.com.matheusgusmao.incometax.web.dto.dependent;

import br.com.matheusgusmao.incometax.domain.model.dependent.Dependent;

import java.time.LocalDate;

public record DependentResponse(Long id, String name, String cpf, LocalDate birthDate) {

    public static DependentResponse from(Dependent dependent) {
        return new DependentResponse(
            dependent.getId(),
            dependent.getName(),
            dependent.getCpf().getValue(),
            dependent.getBirthDate()
        );
    }
}