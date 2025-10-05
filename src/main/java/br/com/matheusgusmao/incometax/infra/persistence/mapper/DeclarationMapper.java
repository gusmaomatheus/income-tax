package br.com.matheusgusmao.incometax.infra.persistence.mapper;

import br.com.matheusgusmao.incometax.domain.model.declaration.Declaration;
import br.com.matheusgusmao.incometax.infra.persistence.entity.declaration.DeclarationEntity;
import org.springframework.stereotype.Component;

@Component
public class DeclarationMapper {

    public DeclarationEntity toEntity(Declaration domain) {
        if (domain == null) return null;

        DeclarationEntity entity = new DeclarationEntity();
        entity.setId(domain.getId());
        entity.setTaxpayerId(domain.getTaxpayerId());
        entity.setYear(domain.getYear());
        entity.setStatus(domain.getStatus());
        return entity;
    }

    public Declaration toDomain(DeclarationEntity entity) {
        if (entity == null) return null;

        return new Declaration(entity.getId(), entity.getTaxpayerId(), entity.getYear(), entity.getStatus());
    }
}