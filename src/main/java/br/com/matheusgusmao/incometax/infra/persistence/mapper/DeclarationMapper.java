package br.com.matheusgusmao.incometax.infra.persistence.mapper;

import br.com.matheusgusmao.incometax.domain.model.declaration.Declaration;
import br.com.matheusgusmao.incometax.infra.persistence.entity.declaration.DeclarationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class DeclarationMapper {

    private final IncomeMapper incomeMapper;

    @Autowired
    public DeclarationMapper(IncomeMapper incomeMapper) {
        this.incomeMapper = incomeMapper;
    }

    public DeclarationMapper() {
        this.incomeMapper = new IncomeMapper();
    }

    public DeclarationEntity toEntity(Declaration domain) {
        if (domain == null) return null;

        DeclarationEntity entity = new DeclarationEntity();
        entity.setId(domain.getId());
        entity.setTaxpayerId(domain.getTaxpayerId());
        entity.setYear(domain.getYear());
        entity.setStatus(domain.getStatus());

        if (domain.getIncomes() != null) {
            entity.setIncomes(domain.getIncomes().stream()
                    .map(income -> incomeMapper.toEntity(income, entity))
                    .collect(Collectors.toList()));
        }

        return entity;
    }

    public Declaration toDomain(DeclarationEntity entity) {
        if (entity == null) return null;

        Declaration domain = new Declaration(entity.getId(), entity.getTaxpayerId(), entity.getYear(), entity.getStatus());
        if (entity.getIncomes() != null) {
            entity.getIncomes().stream()
                    .map(incomeMapper::toDomain)
                    .forEach(domain::addIncome);
        }

        return domain;
    }
}