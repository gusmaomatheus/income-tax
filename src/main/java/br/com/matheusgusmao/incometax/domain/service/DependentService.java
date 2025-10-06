package br.com.matheusgusmao.incometax.domain.service;

import br.com.matheusgusmao.incometax.domain.model.declaration.Dependent;
import br.com.matheusgusmao.incometax.infra.persistence.entity.declaration.DeclarationEntity;
import br.com.matheusgusmao.incometax.infra.persistence.entity.declaration.DependentEntity;
import br.com.matheusgusmao.incometax.infra.persistence.mapper.DependentMapper;
import br.com.matheusgusmao.incometax.infra.persistence.repository.DependentRepository;
import br.com.matheusgusmao.incometax.infra.persistence.repository.DeclarationRepository;
import br.com.matheusgusmao.incometax.web.dto.declaration.CreateDependentRequest;
import br.com.matheusgusmao.incometax.web.dto.declaration.DependentResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        if (!isValidCpf(request.getCpf())) {
            throw new IllegalArgumentException("CPF inválido");
        }

        Optional<DependentEntity> existing = dependentRepository.findByCpf(request.getCpf());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Dependente já cadastrado");
        }

        DeclarationEntity declaration = declarationRepository.findById(declarationId)
                .orElseThrow(() -> new IllegalArgumentException("Declaração não encontrada"));

        Dependent dependent = new Dependent(request.getName(), request.getCpf(), request.getBirthDate());
        DependentEntity dependentEntity = dependentMapper.toEntity(dependent);
        dependentEntity.setDeclaration(declaration);

        dependentEntity = dependentRepository.save(dependentEntity);

        Dependent savedDependent = dependentMapper.toDomain(dependentEntity);

        DependentResponse response = new DependentResponse();
        response.setId(savedDependent.getId());
        response.setName(savedDependent.getName());
        response.setCpf(savedDependent.getCpf());
        response.setBirthDate(savedDependent.getBirthDate());
        return response;
    }

    private boolean isValidCpf(String cpf) {
        return cpf != null && cpf.matches("\\d{11}");
    }
}