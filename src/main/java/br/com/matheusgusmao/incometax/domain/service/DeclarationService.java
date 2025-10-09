package br.com.matheusgusmao.incometax.domain.service;

import br.com.matheusgusmao.incometax.domain.expense.DeductibleExpense;
import br.com.matheusgusmao.incometax.domain.model.declaration.Declaration;
import br.com.matheusgusmao.incometax.domain.model.income.Income;
import br.com.matheusgusmao.incometax.infra.exception.custom.EntityAlreadyExistsException;
import br.com.matheusgusmao.incometax.infra.persistence.entity.declaration.DeclarationEntity;
import br.com.matheusgusmao.incometax.infra.persistence.mapper.DeclarationMapper;
import br.com.matheusgusmao.incometax.infra.persistence.repository.DeclarationRepository;
import jakarta.persistence.EntityNotFoundException;
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

        Declaration newDeclaration = new Declaration(taxpayerId, year);

        DeclarationEntity entityToSave = declarationMapper.toEntity(newDeclaration);
        DeclarationEntity savedEntity = declarationRepository.save(entityToSave);

        return declarationMapper.toDomain(savedEntity);
    }
    @Transactional
    public Declaration addIncome(Long declarationId, Income income) {
        DeclarationEntity declarationEntity = declarationRepository.findById(declarationId)
                .orElseThrow(() -> new EntityNotFoundException("Declaration not found with id: " + declarationId));

        Declaration declarationDomain = declarationMapper.toDomain(declarationEntity);
        declarationDomain.addIncome(income);

        DeclarationEntity entityToSave = declarationMapper.toEntity(declarationDomain);
        DeclarationEntity savedEntity = declarationRepository.save(entityToSave);

        return declarationMapper.toDomain(savedEntity);
    }

    @Transactional
    public Declaration removeIncome(Long declarationId, Long incomeId) {
        DeclarationEntity declarationEntity = declarationRepository.findById(declarationId)
                .orElseThrow(() -> new EntityNotFoundException("Declaration not found with id: " + declarationId));

        Declaration declarationDomain = declarationMapper.toDomain(declarationEntity);
        declarationDomain.removeIncome(incomeId);

        DeclarationEntity entityToSave = declarationMapper.toEntity(declarationDomain);
        DeclarationEntity savedEntity = declarationRepository.save(entityToSave);

        return declarationMapper.toDomain(savedEntity);
    }
    @Transactional
    public Declaration addDeductibleExpense(Long declarationId, DeductibleExpense expense) {
        DeclarationEntity declarationEntity = declarationRepository.findById(declarationId)
                .orElseThrow(() -> new EntityNotFoundException("Declaration not found with id: " + declarationId));

        Declaration declarationDomain = declarationMapper.toDomain(declarationEntity);
        declarationDomain.addDeductibleExpense(expense);

        DeclarationEntity entityToSave = declarationMapper.toEntity(declarationDomain);
        DeclarationEntity savedEntity = declarationRepository.save(entityToSave);

        return declarationMapper.toDomain(savedEntity);
    }

    @Transactional
    public Declaration removeDeductibleExpense(Long declarationId, Long expenseId) {
        DeclarationEntity declarationEntity = declarationRepository.findById(declarationId)
                .orElseThrow(() -> new EntityNotFoundException("Declaration not found with id: " + declarationId));

        Declaration declarationDomain = declarationMapper.toDomain(declarationEntity);
        declarationDomain.removeDeductibleExpense(expenseId);

        DeclarationEntity entityToSave = declarationMapper.toEntity(declarationDomain);
        DeclarationEntity savedEntity = declarationRepository.save(entityToSave);

        return declarationMapper.toDomain(savedEntity);

    public List<DeclarationHistoryResponse> getDeclarationHistory(UUID taxpayerId) {
        List<DeclarationEntity> declarations = declarationRepository.findAllByTaxpayerId(taxpayerId);
        return declarations.stream()
                .map(d -> new DeclarationHistoryResponse(d.getYear(), d.getStatus().name()))
                .toList();
    }

    public Declaration submitDeclaration(Long declarationId) {
        DeclarationEntity entity = declarationRepository.findById(declarationId)
                .orElseThrow(() -> new EntityNotFoundException("Declaração não encontrada"));
        Declaration declaration = declarationMapper.toDomain(entity);

        if (declaration.getIncomes() == null || declaration.getIncomes().isEmpty()) {
            throw new IllegalArgumentException("Informe seus rendimentos");
        }

        declarationRepository.save(entity);
        return declaration;
    }
}