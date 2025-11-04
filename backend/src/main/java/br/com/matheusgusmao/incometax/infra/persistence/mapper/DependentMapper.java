package br.com.matheusgusmao.incometax.infra.persistence.mapper;

import br.com.matheusgusmao.incometax.domain.model.dependent.Cpf;
import br.com.matheusgusmao.incometax.domain.model.dependent.Dependent;
import br.com.matheusgusmao.incometax.infra.persistence.entity.declaration.DeclarationEntity;
import br.com.matheusgusmao.incometax.infra.persistence.entity.dependent.DependentEntity;
import org.springframework.stereotype.Component;

@Component
public class DependentMapper {

    public DependentEntity toEntity(Dependent dependent, DeclarationEntity declaration) {
        DependentEntity entity = new DependentEntity();
        entity.setId(dependent.getId());
        entity.setName(dependent.getName());
        entity.setCpf(dependent.getCpf().getValue());
        entity.setBirthDate(dependent.getBirthDate());
        entity.setDeclaration(declaration);
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