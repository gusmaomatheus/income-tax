package br.com.matheusgusmao.incometax.domain.model.declaration;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Dependent {
    private Long id;
    private String name;
    private Cpf cpf;
    private LocalDate birthDate;

    public Dependent(Long id, String name, Cpf cpf, LocalDate birthDate) {
        this.id = id;
        this.name = name;
        this.cpf = cpf;
        this.birthDate = birthDate;
    }

    public Dependent(String name, Cpf cpf, LocalDate birthDate) {
        this(null, name, cpf, birthDate);
    }
}