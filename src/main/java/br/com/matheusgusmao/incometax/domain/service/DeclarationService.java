package br.com.matheusgusmao.incometax.domain.service;

import br.com.matheusgusmao.incometax.domain.model.dependent.Dependent;
import br.com.matheusgusmao.incometax.domain.model.expense.DeductibleExpense;
import br.com.matheusgusmao.incometax.domain.model.declaration.Declaration;
import br.com.matheusgusmao.incometax.domain.model.income.Income;
import br.com.matheusgusmao.incometax.infra.exception.custom.EntityAlreadyExistsException;
import br.com.matheusgusmao.incometax.infra.persistence.entity.declaration.DeclarationEntity;
import br.com.matheusgusmao.incometax.infra.persistence.mapper.DeclarationMapper;
import br.com.matheusgusmao.incometax.infra.persistence.repository.DeclarationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.matheusgusmao.incometax.web.dto.declaration.DeclarationHistoryResponse;

import java.util.List;
import java.util.UUID;

@Service
public class DeclarationService {

    private final DeclarationRepository declarationRepository;
    private final DeclarationMapper declarationMapper;

    public DeclarationService(final DeclarationRepository declarationRepository, final DeclarationMapper declarationMapper) {
        this.declarationRepository = declarationRepository;
        this.declarationMapper = declarationMapper;
    }

    @Transactional
    public Declaration createNewDeclaration(UUID taxpayerId, int year) {
        if (declarationRepository.existsByTaxpayerIdAndYear(taxpayerId, year)) {
            throw new EntityAlreadyExistsException("A declaration for the given taxpayer and year already exists.");
        }

        var newDeclaration = new Declaration(taxpayerId, year);
        var entityToSave = declarationMapper.toEntity(newDeclaration);
        var savedEntity = declarationRepository.save(entityToSave);

        return declarationMapper.toDomain(savedEntity);
    }

    @Transactional
    public Declaration addIncome(Long declarationId, Income income) {
        var declarationEntity = declarationRepository.findById(declarationId)
                .orElseThrow(() -> new EntityNotFoundException("Declaration not found with id: " + declarationId));

        var declarationDomain = declarationMapper.toDomain(declarationEntity);
        declarationDomain.addIncome(income);

        var entityToSave = declarationMapper.toEntity(declarationDomain);
        var savedEntity = declarationRepository.save(entityToSave);

        return declarationMapper.toDomain(savedEntity);
    }

    @Transactional
    public Declaration removeIncome(Long declarationId, Long incomeId) {
        var declarationEntity = declarationRepository.findById(declarationId)
                .orElseThrow(() -> new EntityNotFoundException("Declaration not found with id: " + declarationId));

        var declarationDomain = declarationMapper.toDomain(declarationEntity);
        declarationDomain.removeIncome(incomeId);

        var entityToSave = declarationMapper.toEntity(declarationDomain);
        var savedEntity = declarationRepository.save(entityToSave);

        return declarationMapper.toDomain(savedEntity);
    }

    @Transactional
    public Declaration addDeductibleExpense(Long declarationId, DeductibleExpense expense) {
        var declarationEntity = declarationRepository.findById(declarationId)
                .orElseThrow(() -> new EntityNotFoundException("Declaration not found with id: " + declarationId));

        var declarationDomain = declarationMapper.toDomain(declarationEntity);
        declarationDomain.addDeductibleExpense(expense);

        var entityToSave = declarationMapper.toEntity(declarationDomain);
        var savedEntity = declarationRepository.save(entityToSave);

        return declarationMapper.toDomain(savedEntity);
    }

    @Transactional
    public Declaration removeDeductibleExpense(Long declarationId, Long expenseId) {
        var declarationEntity = declarationRepository.findById(declarationId)
                .orElseThrow(() -> new EntityNotFoundException("Declaration not found with id: " + declarationId));

        var declarationDomain = declarationMapper.toDomain(declarationEntity);
        declarationDomain.removeDeductibleExpense(expenseId);

        var entityToSave = declarationMapper.toEntity(declarationDomain);
        var savedEntity = declarationRepository.save(entityToSave);

        return declarationMapper.toDomain(savedEntity);
    }

    @Transactional
    public Declaration addDependent(Long declarationId, Dependent dependent) {
        var declarationEntity = declarationRepository.findById(declarationId)
                .orElseThrow(() -> new EntityNotFoundException("Declaration not found with id: " + declarationId));

        var declarationDomain = declarationMapper.toDomain(declarationEntity);
        declarationDomain.addDependent(dependent);

        var entityToSave = declarationMapper.toEntity(declarationDomain);
        var savedEntity = declarationRepository.save(entityToSave);

        return declarationMapper.toDomain(savedEntity);
    }

    @Transactional
    public Declaration removeDependent(Long declarationId, Long dependentId) {
        var declarationEntity = declarationRepository.findById(declarationId)
                .orElseThrow(() -> new EntityNotFoundException("Declaration not found with id: " + declarationId));

        var declarationDomain = declarationMapper.toDomain(declarationEntity);
        declarationDomain.removeDependent(dependentId);

        var entityToSave = declarationMapper.toEntity(declarationDomain);
        var savedEntity = declarationRepository.save(entityToSave);

        return declarationMapper.toDomain(savedEntity);
    }

    @Transactional
    public Declaration submitDeclaration(Long declarationId, UUID taxpayerId) {
        var declarationEntity = findAndValidateOwnership(declarationId, taxpayerId);

        var declarationDomain = declarationMapper.toDomain(declarationEntity);
        declarationDomain.submit();

        var entityToSave = declarationMapper.toEntity(declarationDomain);
        var savedEntity = declarationRepository.save(entityToSave);

        return declarationMapper.toDomain(savedEntity);
    }

    public Declaration findById(Long declarationId) {
        var declarationEntity = declarationRepository.findById(declarationId)
                .orElseThrow(() -> new EntityNotFoundException("Declaration not found with id: " + declarationId));
        return declarationMapper.toDomain(declarationEntity);
    }

    public List<DeclarationHistoryResponse> getDeclarationHistory(UUID taxpayerId) {
        var declarations = declarationRepository.findAllByTaxpayerId(taxpayerId);
        return declarations.stream()
                .map(d -> new DeclarationHistoryResponse(d.getYear(), d.getStatus().name()))
                .toList();
    }

    private DeclarationEntity findAndValidateOwnership(Long declarationId, UUID taxpayerId) {
        var declarationEntity = declarationRepository.findById(declarationId)
                .orElseThrow(() -> new EntityNotFoundException("Declaration not found with id: " + declarationId));

        if (!declarationEntity.getTaxpayerId().equals(taxpayerId)) {
            throw new AccessDeniedException("User is not authorized to modify this declaration.");
        }
        return declarationEntity;
    }
}