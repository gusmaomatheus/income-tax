package br.com.matheusgusmao.incometax.infra.persistence.mapper;

import br.com.matheusgusmao.incometax.domain.model.expense.DeductibleExpense;
import br.com.matheusgusmao.incometax.infra.persistence.entity.declaration.DeclarationEntity;
import br.com.matheusgusmao.incometax.infra.persistence.entity.expense.DeductibleExpenseEntity;
import org.springframework.stereotype.Component;

@Component
public class DeductibleExpenseMapper {
    public DeductibleExpenseEntity toEntity(DeductibleExpense domain, DeclarationEntity declaration) {
        if (domain == null) return null;

        DeductibleExpenseEntity entity = new DeductibleExpenseEntity();
        entity.setId(domain.getId());
        entity.setDescription(domain.getDescription());
        entity.setType(domain.getType());
        entity.setValue(domain.getValue());
        entity.setDeclaration(declaration);
        return entity;
    }

    public DeductibleExpense toDomain(DeductibleExpenseEntity entity) {
        if (entity == null) return null;

        return new DeductibleExpense(entity.getId(), entity.getDescription(), entity.getType(), entity.getValue());
    }
}