package br.com.matheusgusmao.incometax.domain.model.declaration;

import java.time.LocalDate;

public class Dependent {
    private Long id;
    private String name;
    private String cpf;
    private LocalDate birthDate;

    public Dependent(Long id, String name, String cpf, LocalDate birthDate) {
        this.id = id;
        this.name = name;
        this.cpf = cpf;
        this.birthDate = birthDate;
    }

    public Dependent(String name, String cpf, LocalDate birthDate) {
        this(null, name, cpf, birthDate);
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCpf() { return cpf; }
    public LocalDate getBirthDate() { return birthDate; }
}