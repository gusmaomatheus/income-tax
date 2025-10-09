package br.com.matheusgusmao.incometax.infra.persistence.mapper;

import br.com.matheusgusmao.incometax.domain.expense.DeductibleExpense;
import br.com.matheusgusmao.incometax.infra.persistence.entity.expense.DeductibleExpenseEntity;
import org.springframework.stereotype.Component;

@Component
public class DeductibleExpenseMapper {
    public DeductibleExpenseEntity toEntity(DeductibleExpense domain) {
        if (domain == null) return null;

        DeductibleExpenseEntity entity = new DeductibleExpenseEntity();
        entity.setId(domain.getId());
        entity.setDescription(domain.getDescription());
        entity.setType(domain.getType());
        entity.setValue(domain.getValue());
        return entity;
    }

    public DeductibleExpense toDomain(DeductibleExpenseEntity entity) {
        if (entity == null) return null;

        return new DeductibleExpense(entity.getId(), entity.getDescription(), entity.getType(), entity.getValue());
    }
}
