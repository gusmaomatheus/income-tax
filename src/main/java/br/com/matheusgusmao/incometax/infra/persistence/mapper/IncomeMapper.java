package br.com.matheusgusmao.incometax.infra.persistence.mapper;

import br.com.matheusgusmao.incometax.domain.model.income.Income;
import br.com.matheusgusmao.incometax.infra.persistence.entity.declaration.DeclarationEntity;
import br.com.matheusgusmao.incometax.infra.persistence.entity.income.IncomeEntity;

public class IncomeMapper {
    public IncomeEntity toEntity(Income domain, DeclarationEntity declarationEntity) {
        if (domain == null) return null;

        IncomeEntity entity = new IncomeEntity();
        entity.setId(domain.getId());
        entity.setPayingSource(domain.getPayingSource());
        entity.setType(domain.getType());
        entity.setValue(domain.getValue());
        entity.setDeclaration(declarationEntity);
        return entity;
    }

    public Income toDomain(IncomeEntity entity) {
        if (entity == null) return null;

        return new Income(entity.getId(), entity.getPayingSource(), entity.getType(), entity.getValue());
    }
}
