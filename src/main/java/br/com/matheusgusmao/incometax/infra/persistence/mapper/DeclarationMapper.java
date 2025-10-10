package br.com.matheusgusmao.incometax.infra.persistence.mapper;

import br.com.matheusgusmao.incometax.domain.model.declaration.Declaration;
import br.com.matheusgusmao.incometax.infra.persistence.entity.declaration.DeclarationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class DeclarationMapper {

    private final IncomeMapper incomeMapper;
    private final DeductibleExpenseMapper deductibleExpenseMapper;


    @Autowired
    public DeclarationMapper(IncomeMapper incomeMapper,  DeductibleExpenseMapper deductibleExpenseMapper) {
        this.incomeMapper = incomeMapper;
        this.deductibleExpenseMapper = new DeductibleExpenseMapper();
    }


    public DeclarationEntity toEntity(Declaration domain) {
        if (domain == null) return null;

        DeclarationEntity entity = new DeclarationEntity();
        entity.setId(domain.getId());
        entity.setTaxpayerId(domain.getTaxpayerId());
        entity.setYear(domain.getYear());
        entity.setStatus(domain.getStatus());
        entity.setDeliveryDate(domain.getDeliveryDate());

        if (domain.getIncomes() != null) {
            entity.setIncomes(domain.getIncomes().stream()
                    .map(income -> incomeMapper.toEntity(income, entity))
                    .collect(Collectors.toList()));
        }

        if (domain.getDeductibleExpenses() != null) {
            entity.setDeductibleExpenses(domain.getDeductibleExpenses().stream()
                    .map(deductibleExpenseMapper::toEntity)
                    .peek(expenseEntity -> expenseEntity.setDeclaration(entity))
                    .collect(Collectors.toList()));
        }

        return entity;
    }

    public Declaration toDomain(DeclarationEntity entity) {
        if (entity == null) return null;

        Declaration domain = new Declaration(entity.getId(), entity.getTaxpayerId(), entity.getYear(), entity.getStatus(), entity.getDeliveryDate());
        if (entity.getIncomes() != null) {
            entity.getIncomes().stream()
                    .map(incomeMapper::toDomain)
                    .forEach(domain::addIncome);
        }
        if (entity.getDeductibleExpenses() != null) {
            entity.getDeductibleExpenses().stream()
                    .map(deductibleExpenseMapper::toDomain)
                    .forEach(domain::addDeductibleExpense);
        }

        return domain;
    }
}