package br.com.matheusgusmao.incometax.domain.service;

import br.com.matheusgusmao.incometax.domain.model.dependent.Dependent;
import br.com.matheusgusmao.incometax.domain.model.dependent.Cpf;
import br.com.matheusgusmao.incometax.infra.persistence.entity.declaration.DeclarationEntity;
import br.com.matheusgusmao.incometax.infra.persistence.entity.dependent.DependentEntity;
import br.com.matheusgusmao.incometax.infra.persistence.mapper.DependentMapper;
import br.com.matheusgusmao.incometax.infra.persistence.repository.DependentRepository;
import br.com.matheusgusmao.incometax.infra.persistence.repository.DeclarationRepository;
import br.com.matheusgusmao.incometax.web.dto.dependent.CreateDependentRequest;
import br.com.matheusgusmao.incometax.web.dto.dependent.DependentResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class DependentService {

    private final DependentRepository dependentRepository;
    private final DeclarationRepository declarationRepository;
    private final DependentMapper dependentMapper;

    public DependentService(DependentRepository dependentRepository, DeclarationRepository declarationRepository, DependentMapper dependentMapper) {
        this.dependentRepository = dependentRepository;
        this.declarationRepository = declarationRepository;
        this.dependentMapper = dependentMapper;
    }

    @Transactional
    public DependentResponse addDependent(Long declarationId, CreateDependentRequest request) {
        var cpf = new Cpf(request.cpf());

        var existing = dependentRepository.findByCpf(cpf.getValue());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Dependente já cadastrado");
        }

        var declaration = declarationRepository.findById(declarationId)
                .orElseThrow(() -> new IllegalArgumentException("Declaração não encontrada"));

        var dependent = new Dependent(request.name(), cpf, request.birthDate());
        var dependentEntity = dependentMapper.toEntity(dependent);
        dependentEntity.setDeclaration(declaration);

        dependentEntity = dependentRepository.save(dependentEntity);

        var savedDependent = dependentMapper.toDomain(dependentEntity);

        return DependentResponse.from(savedDependent);
    }
}