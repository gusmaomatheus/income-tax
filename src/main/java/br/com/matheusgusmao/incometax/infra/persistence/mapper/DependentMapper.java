package br.com.matheusgusmao.incometax.infra.persistence.mapper;

import br.com.matheusgusmao.incometax.domain.model.declaration.Cpf;
import br.com.matheusgusmao.incometax.domain.model.declaration.Dependent;
import br.com.matheusgusmao.incometax.infra.persistence.entity.declaration.DependentEntity;

public class DependentMapper {

    public DependentEntity toEntity(Dependent dependent) {
        DependentEntity entity = new DependentEntity();
        entity.setId(dependent.getId());
        entity.setName(dependent.getName());
        entity.setCpf(dependent.getCpf().getValue()); 
        entity.setBirthDate(dependent.getBirthDate());
        return entity;
    }

    public Dependent toDomain(DependentEntity entity) {
        return new Dependent(
                entity.getId(),
                entity.getName(),
                new Cpf(entity.getCpf()),
                entity.getBirthDate()
        );
    }
}