package br.com.matheusgusmao.incometax.service;

import br.com.matheusgusmao.incometax.domain.model.declaration.Declaration;
import br.com.matheusgusmao.incometax.domain.model.declaration.DeclarationStatus;
import br.com.matheusgusmao.incometax.domain.service.DeclarationService;
import br.com.matheusgusmao.incometax.infra.exception.custom.EntityAlreadyExistsException;
import br.com.matheusgusmao.incometax.infra.persistence.entity.declaration.DeclarationEntity;
import br.com.matheusgusmao.incometax.infra.persistence.mapper.DeclarationMapper;
import br.com.matheusgusmao.incometax.infra.persistence.repository.DeclarationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

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

}