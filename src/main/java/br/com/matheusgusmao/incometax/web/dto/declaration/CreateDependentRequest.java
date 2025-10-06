package br.com.matheusgusmao.incometax.web.dto.declaration;

import java.time.LocalDate;

public class CreateDependentRequest {
    private String name;
    private String cpf;
    private LocalDate birthDate;
}