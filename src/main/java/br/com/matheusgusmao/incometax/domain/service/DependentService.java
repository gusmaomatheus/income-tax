package br.com.matheusgusmao.incometax.domain.service;

import br.com.matheusgusmao.incometax.domain.model.dependent.Dependent;
import br.com.matheusgusmao.incometax.infra.persistence.entity.dependent.DeclarationEntity;
import br.com.matheusgusmao.incometax.infra.persistence.entity.dependent.DependentEntity;
import br.com.matheusgusmao.incometax.infra.persistence.mapper.DependentMapper;
import br.com.matheusgusmao.incometax.infra.persistence.repository.DependentRepository;
import br.com.matheusgusmao.incometax.infra.persistence.repository.DeclarationRepository;
import br.com.matheusgusmao.incometax.web.dto.dependent.CreateDependentRequest;
import br.com.matheusgusmao.incometax.web.dto.dependent.DependentResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.matheusgusmao.incometax.domain.model.dependent.Cpf;

import java.util.Optional;

@Service
public class DependentService {

    private final DependentRepository dependentRepository;
    private final DeclarationRepository declarationRepository;
    private final DependentMapper dependentMapper = new DependentMapper();

    public DependentService(DependentRepository dependentRepository, DeclarationRepository declarationRepository) {
        this.dependentRepository = dependentRepository;
        this.declarationRepository = declarationRepository;
    }

    @Transactional
    public DependentResponse addDependent(Long declarationId, CreateDependentRequest request) {
        Cpf cpf = new Cpf(request.cpf());

        Optional<DependentEntity> existing = dependentRepository.findByCpf(cpf.getValue());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Dependente já cadastrado");
        }

        DeclarationEntity declaration = declarationRepository.findById(declarationId)
                .orElseThrow(() -> new IllegalArgumentException("Declaração não encontrada"));

        Dependent dependent = new Dependent(request.name(), cpf, request.birthDate());
        DependentEntity dependentEntity = dependentMapper.toEntity(dependent);
        dependentEntity.setDeclaration(declaration);

        dependentEntity = dependentRepository.save(dependentEntity);

        Dependent savedDependent = dependentMapper.toDomain(dependentEntity);

        return DependentResponse.from(savedDependent);
    }
}