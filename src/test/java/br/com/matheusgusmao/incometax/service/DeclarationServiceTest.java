package br.com.matheusgusmao.incometax.service;

import br.com.matheusgusmao.incometax.domain.model.declaration.Declaration;
import br.com.matheusgusmao.incometax.domain.model.declaration.DeclarationStatus;
import br.com.matheusgusmao.incometax.domain.model.income.Income;
import br.com.matheusgusmao.incometax.domain.model.income.IncomeType;
import br.com.matheusgusmao.incometax.domain.service.DeclarationService;
import br.com.matheusgusmao.incometax.infra.exception.custom.EntityAlreadyExistsException;
import br.com.matheusgusmao.incometax.infra.persistence.entity.declaration.DeclarationEntity;
import br.com.matheusgusmao.incometax.infra.persistence.mapper.DeclarationMapper;
import br.com.matheusgusmao.incometax.infra.persistence.repository.DeclarationRepository;
import br.com.matheusgusmao.incometax.web.dto.declaration.DeclarationHistoryResponse;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeclarationServiceTest {

    @Mock
    private DeclarationRepository declarationRepository;

    @Spy
    private DeclarationMapper declarationMapper = new DeclarationMapper();

    @InjectMocks
    private DeclarationService declarationService;

    @Test
    @DisplayName("US1-[Scenario] Should create new declaration successfully")
    void shouldCreateNewDeclarationSuccessfullyWhenNonExistent() {
        final UUID taxpayerId = UUID.randomUUID();
        final int year = 2025;

        when(declarationRepository.existsByTaxpayerIdAndYear(taxpayerId, year)).thenReturn(false);
        when(declarationRepository.save(any(DeclarationEntity.class))).thenAnswer(invocation -> {
            DeclarationEntity entity = invocation.getArgument(0);
            entity.setId(1L);
            return entity;
        });

        Declaration result = declarationService.createNewDeclaration(taxpayerId, year);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(1L, result.getId());
        verify(declarationRepository).existsByTaxpayerIdAndYear(taxpayerId, year);
        verify(declarationRepository).save(any(DeclarationEntity.class));
    }

    @Test
    @DisplayName("US1-[Scenario] Should prevent duplicate declaration in the same year")
    void shouldThrowExceptionWhenDeclarationForSameYearAlreadyExists() {
        final UUID taxpayerId = UUID.randomUUID();
        final int year = 2025;
        when(declarationRepository.existsByTaxpayerIdAndYear(taxpayerId, year)).thenReturn(true);

        assertThrows(EntityAlreadyExistsException.class,
                () -> declarationService.createNewDeclaration(taxpayerId, year)
        );

        verify(declarationRepository, never()).save(any(DeclarationEntity.class));
    }

    @Test
    @DisplayName("US2-[Scenario] Should add a valid income to a declaration")
    void shouldAddValidIncomeToDeclaration() {
        final Long declarationId = 1L;
        final Income newIncome = new Income("Company A", IncomeType.SALARY, new BigDecimal("50000.00"));

        DeclarationEntity existingDeclarationEntity = new DeclarationEntity();
        existingDeclarationEntity.setId(declarationId);
        existingDeclarationEntity.setStatus(DeclarationStatus.EDITING);
        existingDeclarationEntity.setTaxpayerId(UUID.randomUUID());
        existingDeclarationEntity.setYear(2025);

        when(declarationRepository.findById(declarationId)).thenReturn(Optional.of(existingDeclarationEntity));
        when(declarationRepository.save(any(DeclarationEntity.class))).thenAnswer(i -> i.getArgument(0));

        Declaration updatedDeclaration = declarationService.addIncome(declarationId, newIncome);

        assertNotNull(updatedDeclaration);
        assertEquals(1, updatedDeclaration.getIncomes().size());
        assertEquals("Company A", updatedDeclaration.getIncomes().get(0).getPayingSource());
        verify(declarationRepository).findById(declarationId);
        verify(declarationRepository).save(any(DeclarationEntity.class));
    }

    @Test
    @DisplayName("US2-[Scenario] Should prevent adding income with a negative value")
    void shouldPreventAddingIncomeWithNegativeValue() {
        final Long declarationId = 1L;

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Income("Company B", IncomeType.OTHER, new BigDecimal("-100.00"));
        });

        assertEquals("Income value cannot be negative.", exception.getMessage());
        verify(declarationRepository, never()).findById(any());
        verify(declarationRepository, never()).save(any());
    }

    @Test
    @DisplayName("US2-[Scenario] Should allow different incomes from the same source")
    void shouldAllowDifferentIncomesFromSameSource() {
        final Long declarationId = 1L;
        final Income income1 = new Income("Company C", IncomeType.SALARY, new BigDecimal("60000.00"));
        final Income income2 = new Income("Company C", IncomeType.VACATION, new BigDecimal("5000.00"));

        DeclarationEntity existingDeclarationEntity = new DeclarationEntity();
        existingDeclarationEntity.setId(declarationId);
        existingDeclarationEntity.setStatus(DeclarationStatus.EDITING);
        existingDeclarationEntity.setTaxpayerId(UUID.randomUUID());
        existingDeclarationEntity.setYear(2025);

        when(declarationRepository.findById(declarationId)).thenReturn(Optional.of(existingDeclarationEntity));
        when(declarationRepository.save(any(DeclarationEntity.class))).thenAnswer(i -> i.getArgument(0));

        declarationService.addIncome(declarationId, income1);

        Declaration updatedDeclaration = declarationService.addIncome(declarationId, income2);

        assertNotNull(updatedDeclaration);
        assertEquals(2, updatedDeclaration.getIncomes().size());
        verify(declarationRepository, times(2)).findById(declarationId);
        verify(declarationRepository, times(2)).save(any(DeclarationEntity.class));
    }

    @Test
    @DisplayName("US2-[Scenario] Should remove an income successfully")
    void shouldRemoveIncomeSuccessfully() {
        final Long declarationId = 1L;
        final Long incomeIdToRemove = 10L;

        DeclarationEntity existingDeclarationEntity = new DeclarationEntity();
        existingDeclarationEntity.setId(declarationId);
        existingDeclarationEntity.setStatus(DeclarationStatus.EDITING);

        Declaration declarationDomain = new Declaration(declarationId, UUID.randomUUID(), 2025, DeclarationStatus.EDITING);
        Income income = new Income(incomeIdToRemove, "Company A", IncomeType.SALARY, new BigDecimal("50000"));
        declarationDomain.addIncome(income);

        when(declarationRepository.findById(declarationId)).thenReturn(Optional.of(existingDeclarationEntity));
        when(declarationMapper.toDomain(any(DeclarationEntity.class))).thenReturn(declarationDomain);
        when(declarationRepository.save(any(DeclarationEntity.class))).thenAnswer(i -> i.getArgument(0));

        Declaration updatedDeclaration = declarationService.removeIncome(declarationId, incomeIdToRemove);

        assertNotNull(updatedDeclaration);
        assertTrue(updatedDeclaration.getIncomes().isEmpty());
        assertThat(updatedDeclaration.getIncomes()).noneMatch(i -> i.getId().equals(incomeIdToRemove));
        verify(declarationRepository).findById(declarationId);
        verify(declarationRepository).save(any(DeclarationEntity.class));
    }

    @Test
    @DisplayName("US2-[Scenario] Should throw exception when trying to remove a non-existent income")
    void shouldThrowExceptionWhenRemovingNonExistentIncome() {
        final Long declarationId = 1L;
        final Long nonExistentIncomeId = 99L;

        DeclarationEntity existingDeclarationEntity = new DeclarationEntity();
        existingDeclarationEntity.setId(declarationId);
        existingDeclarationEntity.setStatus(DeclarationStatus.EDITING);

        Declaration declarationDomain = new Declaration(declarationId, UUID.randomUUID(), 2025, DeclarationStatus.EDITING);

        when(declarationRepository.findById(declarationId)).thenReturn(Optional.of(existingDeclarationEntity));
        when(declarationMapper.toDomain(any(DeclarationEntity.class))).thenReturn(declarationDomain);

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> declarationService.removeIncome(declarationId, nonExistentIncomeId)
        );

        assertEquals("Income not found with id: " + nonExistentIncomeId, exception.getMessage());
        verify(declarationRepository, never()).save(any());
    }

    @Test
    @DisplayName("[Scenario] Should list previous declarations")
    void shouldListPreviousDeclarations() {
        UUID taxpayerId = UUID.randomUUID();
        DeclarationEntity d1 = new DeclarationEntity();
        d1.setId(1L);
        d1.setTaxpayerId(taxpayerId);
        d1.setYear(2022);
        d1.setStatus(DeclarationStatus.EDITING);

        DeclarationEntity d2 = new DeclarationEntity();
        d2.setId(2L);
        d2.setTaxpayerId(taxpayerId);
        d2.setYear(2023);
        d2.setStatus(DeclarationStatus.EDITING);

        when(declarationRepository.findAllByTaxpayerId(taxpayerId)).thenReturn(List.of(d1, d2));

        var result = declarationService.getDeclarationHistory(taxpayerId);

        assertEquals(2, result.size());
        assertEquals(2022, result.get(0).year());
        assertEquals("EDITING", result.get(0).status());
        assertEquals(2023, result.get(1).year());
        assertEquals("EDITING", result.get(1).status());
    }

    @Test
    @DisplayName("[Scenario] Should return empty list and message when no declarations found")
    void shouldReturnEmptyListWhenNoDeclarationsFound() {
        UUID taxpayerId = UUID.randomUUID();
        when(declarationRepository.findAllByTaxpayerId(taxpayerId)).thenReturn(List.of());

        var result = declarationService.getDeclarationHistory(taxpayerId);

        assertTrue(result.isEmpty());
    }
}