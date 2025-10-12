package br.com.matheusgusmao.incometax.infra.persistence.mapper;

import br.com.matheusgusmao.incometax.domain.model.declaration.Declaration;
import br.com.matheusgusmao.incometax.infra.persistence.entity.declaration.DeclarationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class DeclarationMapper {

    private IncomeMapper incomeMapper;
    private DeductibleExpenseMapper deductibleExpenseMapper;
    private DependentMapper dependentMapper;

    public DeclarationMapper() {}

    @Autowired
    public DeclarationMapper(IncomeMapper incomeMapper, DeductibleExpenseMapper deductibleExpenseMapper, DependentMapper dependentMapper) {
        this.incomeMapper = incomeMapper;
        this.deductibleExpenseMapper = deductibleExpenseMapper;
        this.dependentMapper = dependentMapper;
    }


    public DeclarationEntity toEntity(Declaration domain) {
        if (domain == null) return null;

        DeclarationEntity declarationEntity = new DeclarationEntity();
        declarationEntity.setId(domain.getId());
        declarationEntity.setTaxpayerId(domain.getTaxpayerId());
        declarationEntity.setYear(domain.getYear());
        declarationEntity.setStatus(domain.getStatus());
        declarationEntity.setDeliveryDate(domain.getDeliveryDate());

        declarationEntity.setIncomes(domain.getIncomes().stream()
                .map(income -> incomeMapper.toEntity(income, declarationEntity))
                .collect(Collectors.toList()));

        declarationEntity.setDeductibleExpenses(domain.getDeductibleExpenses().stream()
                .map(deductibleExpense ->  deductibleExpenseMapper.toEntity(deductibleExpense, declarationEntity))
                .collect(Collectors.toList()));

        declarationEntity.setDependents(domain.getDependents().stream()
                .map(dependent -> dependentMapper.toEntity(dependent, declarationEntity))
                .collect(Collectors.toList()));

        return declarationEntity;
    }

    public Declaration toDomain(DeclarationEntity entity) {
        if (entity == null) return null;

        Declaration domain = new Declaration(entity.getId(), entity.getTaxpayerId(), entity.getYear(), entity.getStatus(), entity.getDeliveryDate());

        if (!entity.getIncomes().isEmpty()) {
            entity.getIncomes().stream()
                    .map(incomeMapper::toDomain)
                    .forEach(domain::addIncome);
        }

        if (!entity.getDeductibleExpenses().isEmpty()) {
            entity.getDeductibleExpenses().stream()
                    .map(deductibleExpenseMapper::toDomain)
                    .forEach(domain::addDeductibleExpense);
        }

        if (!entity.getDependents().isEmpty()) {
            entity.getDependents().stream()
                    .map(dependentMapper::toDomain)
                    .forEach(domain::addDependent);
        }

        return domain;
    }
}