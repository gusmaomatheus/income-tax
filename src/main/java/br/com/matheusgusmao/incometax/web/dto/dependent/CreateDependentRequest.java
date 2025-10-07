package br.com.matheusgusmao.incometax.web.dto.dependent;

import java.time.LocalDate;

public record CreateDependentRequest(String name,String cpf,LocalDate birthDate) {    
}