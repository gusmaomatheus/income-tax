package br.com.matheusgusmao.incometax.service;

import br.com.matheusgusmao.incometax.domain.model.expense.DeductibleExpense;
import br.com.matheusgusmao.incometax.domain.model.expense.ExpenseType;
import br.com.matheusgusmao.incometax.domain.model.declaration.Declaration;
import br.com.matheusgusmao.incometax.domain.model.declaration.DeclarationStatus;
import br.com.matheusgusmao.incometax.domain.model.income.Income;
import br.com.matheusgusmao.incometax.domain.model.income.IncomeType;
import br.com.matheusgusmao.incometax.domain.service.DeclarationService;
import br.com.matheusgusmao.incometax.infra.exception.custom.EntityAlreadyExistsException;
import br.com.matheusgusmao.incometax.infra.persistence.entity.declaration.DeclarationEntity;
import br.com.matheusgusmao.incometax.infra.persistence.mapper.DeclarationMapper;
import br.com.matheusgusmao.incometax.infra.persistence.mapper.DeductibleExpenseMapper;
import br.com.matheusgusmao.incometax.infra.persistence.mapper.IncomeMapper;
import br.com.matheusgusmao.incometax.infra.persistence.mapper.DependentMapper;
import br.com.matheusgusmao.incometax.infra.persistence.repository.DeclarationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeclarationService")
class DeclarationServiceTest {

    @Mock
    private DeclarationRepository declarationRepository;
    @Mock
    private IncomeMapper incomeMapper;
    @Mock
    private DeductibleExpenseMapper deductibleExpenseMapper;
    @Mock
    private DependentMapper dependentMapper;
    @Spy
    private DeclarationMapper declarationMapper;
    @InjectMocks
    private DeclarationService declarationService;

    @Nested
    @DisplayName("Given a taxpayer wants to create a new declaration")
    class CreateDeclarationTests {

        @Test
        @DisplayName("When taxpayer creates declaration for a new year Then declaration should be created successfully")
        void shouldCreateNewDeclarationSuccessfullyWhenNonExistent() {
            // Given
            var taxpayerId = UUID.randomUUID();
            var year = 2025;

            when(declarationRepository.existsByTaxpayerIdAndYear(taxpayerId, year)).thenReturn(false);
            when(declarationRepository.save(any(DeclarationEntity.class))).thenAnswer(invocation -> {
                var entity = invocation.getArgument(0);
                entity.setId(1L);
                return entity;
            });

            // When
            var result = declarationService.createNewDeclaration(taxpayerId, year);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            verify(declarationRepository).existsByTaxpayerIdAndYear(taxpayerId, year);
            verify(declarationRepository).save(any(DeclarationEntity.class));
        }

        @Test
        @DisplayName("When taxpayer tries to create declaration for existing year Then exception should be thrown")
        void shouldThrowExceptionWhenDeclarationForSameYearAlreadyExists() {
            // Given
            var taxpayerId = UUID.randomUUID();
            var year = 2025;
            when(declarationRepository.existsByTaxpayerIdAndYear(taxpayerId, year)).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> declarationService.createNewDeclaration(taxpayerId, year))
                    .isInstanceOf(EntityAlreadyExistsException.class);

            verify(declarationRepository, never()).save(any(DeclarationEntity.class));
        }
    }

    @Nested
    @DisplayName("Given a taxpayer wants to manage incomes in their declaration")
    class IncomeManagementTests {

        @Test
        @DisplayName("When taxpayer adds valid income Then income should be added successfully")
        void shouldAddValidIncomeToDeclaration() {
            // Given
            var declarationId = 1L;
            var newIncome = new Income("Company A", IncomeType.SALARY, new BigDecimal("50000.00"));

            var existingDeclarationEntity = new DeclarationEntity();
            existingDeclarationEntity.setId(declarationId);
            existingDeclarationEntity.setStatus(DeclarationStatus.EDITING);
            existingDeclarationEntity.setTaxpayerId(UUID.randomUUID());
            existingDeclarationEntity.setYear(2025);

            when(declarationRepository.findById(declarationId)).thenReturn(Optional.of(existingDeclarationEntity));
            when(declarationRepository.save(any(DeclarationEntity.class))).thenAnswer(i -> i.getArgument(0));

            // When
            var updatedDeclaration = declarationService.addIncome(declarationId, newIncome);

            // Then
            assertThat(updatedDeclaration).isNotNull();
            assertThat(updatedDeclaration.getIncomes()).hasSize(1);
            assertThat(updatedDeclaration.getIncomes().get(0).getPayingSource()).isEqualTo("Company A");
            verify(declarationRepository).findById(declarationId);
            verify(declarationRepository).save(any(DeclarationEntity.class));
        }

        @Test
        @DisplayName("When taxpayer tries to add income with negative value Then exception should be thrown")
        void shouldPreventAddingIncomeWithNegativeValue() {
            // Given
            var declarationId = 1L;

            // When & Then
            assertThatThrownBy(() -> new Income("Company B", IncomeType.OTHER, new BigDecimal("-100.00")))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Income value cannot be negative.");

            verify(declarationRepository, never()).findById(any());
            verify(declarationRepository, never()).save(any());
        }

        @Test
        @DisplayName("When taxpayer adds multiple incomes from same source Then all incomes should be added")
        void shouldAllowDifferentIncomesFromSameSource() {
            // Given
            var declarationId = 1L;
            var income1 = new Income("Company C", IncomeType.SALARY, new BigDecimal("60000.00"));
            var income2 = new Income("Company C", IncomeType.VACATION, new BigDecimal("5000.00"));

            var existingDeclarationEntity = new DeclarationEntity();
            existingDeclarationEntity.setId(declarationId);
            existingDeclarationEntity.setStatus(DeclarationStatus.EDITING);
            existingDeclarationEntity.setTaxpayerId(UUID.randomUUID());
            existingDeclarationEntity.setYear(2025);

            when(declarationRepository.findById(declarationId)).thenReturn(Optional.of(existingDeclarationEntity));
            when(declarationRepository.save(any(DeclarationEntity.class))).thenAnswer(i -> i.getArgument(0));

            // When
            declarationService.addIncome(declarationId, income1);
            var updatedDeclaration = declarationService.addIncome(declarationId, income2);

            // Then
            assertThat(updatedDeclaration).isNotNull();
            assertThat(updatedDeclaration.getIncomes()).hasSize(2);
            verify(declarationRepository, times(2)).findById(declarationId);
            verify(declarationRepository, times(2)).save(any(DeclarationEntity.class));
        }

        @Test
        @DisplayName("When taxpayer removes existing income Then income should be removed successfully")
        void shouldRemoveIncomeSuccessfully() {
            // Given
            var declarationId = 1L;
            var incomeIdToRemove = 10L;

            var existingDeclarationEntity = new DeclarationEntity();
            existingDeclarationEntity.setId(declarationId);
            existingDeclarationEntity.setStatus(DeclarationStatus.EDITING);

            var declarationDomain = new Declaration(declarationId, UUID.randomUUID(), 2025, DeclarationStatus.EDITING, null);
            var income = new Income(incomeIdToRemove, "Company A", IncomeType.SALARY, new BigDecimal("50000"));
            declarationDomain.addIncome(income);

            when(declarationRepository.findById(declarationId)).thenReturn(Optional.of(existingDeclarationEntity));
            when(declarationMapper.toDomain(any(DeclarationEntity.class))).thenReturn(declarationDomain);
            when(declarationRepository.save(any(DeclarationEntity.class))).thenAnswer(i -> i.getArgument(0));

            // When
            var updatedDeclaration = declarationService.removeIncome(declarationId, incomeIdToRemove);

            // Then
            assertThat(updatedDeclaration).isNotNull();
            assertThat(updatedDeclaration.getIncomes()).isEmpty();
            assertThat(updatedDeclaration.getIncomes()).noneMatch(i -> i.getId().equals(incomeIdToRemove));
            verify(declarationRepository).findById(declarationId);
            verify(declarationRepository).save(any(DeclarationEntity.class));
        }

        @Test
        @DisplayName("When taxpayer tries to remove non-existent income Then exception should be thrown")
        void shouldThrowExceptionWhenRemovingNonExistentIncome() {
            // Given
            var declarationId = 1L;
            var nonExistentIncomeId = 99L;

            var existingDeclarationEntity = new DeclarationEntity();
            existingDeclarationEntity.setId(declarationId);
            existingDeclarationEntity.setStatus(DeclarationStatus.EDITING);

            var declarationDomain = new Declaration(declarationId, UUID.randomUUID(), 2025, DeclarationStatus.EDITING, null);

            when(declarationRepository.findById(declarationId)).thenReturn(Optional.of(existingDeclarationEntity));
            when(declarationMapper.toDomain(any(DeclarationEntity.class))).thenReturn(declarationDomain);

            // When & Then
            assertThatThrownBy(() -> declarationService.removeIncome(declarationId, nonExistentIncomeId))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Income not found with id: " + nonExistentIncomeId);

            verify(declarationRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Given a taxpayer wants to manage deductible expenses in their declaration")
    class DeductibleExpenseManagementTests {

        @Test
        @DisplayName("When taxpayer adds valid health expense Then expense should be added successfully")
        void shouldAddDeductibleHealthExpenseSuccessfully() {
            // Given
            var declarationId = 1L;
            var existingDeclarationEntity = new DeclarationEntity();
            existingDeclarationEntity.setId(declarationId);

            when(declarationRepository.findById(declarationId)).thenReturn(Optional.of(existingDeclarationEntity));
            when(declarationRepository.save(any(DeclarationEntity.class))).thenAnswer(i -> i.getArgument(0));

            var expense = new DeductibleExpense("Consulta médica", ExpenseType.HEALTH, new BigDecimal("350.00"));

            // When
            var result = declarationService.addDeductibleExpense(declarationId, expense);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getDeductibleExpenses()).hasSize(1);
            assertThat(result.getDeductibleExpenses()).first().usingRecursiveComparison().isEqualTo(expense);
            verify(declarationRepository).findById(declarationId);
            verify(declarationRepository).save(any(DeclarationEntity.class));
        }

        @Test
        @DisplayName("When taxpayer tries to add expense with zero or negative value Then exception should be thrown")
        void shouldRejectExpenseWithZeroValue() {
            // When & Then
            assertThatThrownBy(() -> new DeductibleExpense("Inscrição", ExpenseType.EDUCATION, BigDecimal.ZERO))
                    .isInstanceOf(IllegalArgumentException.class);

            assertThatThrownBy(() -> new DeductibleExpense("Inscrição", ExpenseType.EDUCATION, new BigDecimal("-100")))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("When taxpayer adds non-deductible expense Then expense should be added successfully")
        void shouldAcceptNonDeductibleExpense() {
            // Given
            var declarationId = 1L;
            var existingDeclarationEntity = new DeclarationEntity();
            existingDeclarationEntity.setId(declarationId);

            when(declarationRepository.findById(declarationId)).thenReturn(Optional.of(existingDeclarationEntity));
            when(declarationRepository.save(any(DeclarationEntity.class))).thenAnswer(i -> i.getArgument(0));

            var expense = new DeductibleExpense("Assinatura de revista", ExpenseType.OTHER, new BigDecimal("50.00"));

            // When
            var result = declarationService.addDeductibleExpense(declarationId, expense);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getDeductibleExpenses()).hasSize(1);
            assertThat(result.getDeductibleExpenses().get(0).getType()).isEqualTo(ExpenseType.OTHER);
        }

        @Test
        @DisplayName("When taxpayer removes existing expense Then expense should be removed successfully")
        void shouldRemoveDeductibleExpenseSuccessfully() {
            // Given
            var declarationId = 1L;
            var expenseIdToRemove = 20L;
            var existingDeclarationEntity = new DeclarationEntity();
            existingDeclarationEntity.setId(declarationId);
            existingDeclarationEntity.setStatus(DeclarationStatus.EDITING);

            var declarationDomain = new Declaration(declarationId, UUID.randomUUID(), 2025, DeclarationStatus.EDITING, null);
            var expense = new DeductibleExpense(expenseIdToRemove, "Plano de Saúde", ExpenseType.HEALTH, new BigDecimal("600"));
            declarationDomain.addDeductibleExpense(expense);

            when(declarationRepository.findById(declarationId)).thenReturn(Optional.of(existingDeclarationEntity));
            when(declarationMapper.toDomain(any(DeclarationEntity.class))).thenReturn(declarationDomain);
            when(declarationRepository.save(any(DeclarationEntity.class))).thenAnswer(i -> i.getArgument(0));

            // When
            var updatedDeclaration = declarationService.removeDeductibleExpense(declarationId, expenseIdToRemove);

            // Then
            assertThat(updatedDeclaration).isNotNull();
            assertThat(updatedDeclaration.getDeductibleExpenses()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Given a taxpayer wants to view their declaration history")
    class DeclarationHistoryTests {

        @Test
        @DisplayName("When taxpayer requests declaration history Then all declarations should be returned")
        void shouldListPreviousDeclarations() {
            // Given
            var taxpayerId = UUID.randomUUID();
            var d1 = new DeclarationEntity();
            d1.setId(1L);
            d1.setTaxpayerId(taxpayerId);
            d1.setYear(2022);
            d1.setStatus(DeclarationStatus.EDITING);

            var d2 = new DeclarationEntity();
            d2.setId(2L);
            d2.setTaxpayerId(taxpayerId);
            d2.setYear(2023);
            d2.setStatus(DeclarationStatus.EDITING);

            when(declarationRepository.findAllByTaxpayerId(taxpayerId)).thenReturn(List.of(d1, d2));

            // When
            var result = declarationService.getDeclarationHistory(taxpayerId);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).year()).isEqualTo(2022);
            assertThat(result.get(0).status()).isEqualTo("EDITING");
            assertThat(result.get(1).year()).isEqualTo(2023);
            assertThat(result.get(1).status()).isEqualTo("EDITING");
        }

        @Test
        @DisplayName("When taxpayer has no declarations Then empty list should be returned")
        void shouldReturnEmptyListWhenNoDeclarationsFound() {
            // Given
            var taxpayerId = UUID.randomUUID();
            when(declarationRepository.findAllByTaxpayerId(taxpayerId)).thenReturn(List.of());

            // When
            var result = declarationService.getDeclarationHistory(taxpayerId);

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Given a taxpayer wants to submit their declaration")
    class DeclarationSubmissionTests {

        @Test
        @DisplayName("When taxpayer submits complete declaration Then declaration should be submitted successfully")
        void shouldSubmitDeclarationSuccessfullyWhenComplete() {
            // Given
            var declarationId = 1L;
            var taxpayerId = UUID.randomUUID();
            var declarationEntity = new DeclarationEntity();
            declarationEntity.setId(declarationId);
            declarationEntity.setTaxpayerId(taxpayerId);
            declarationEntity.setStatus(DeclarationStatus.EDITING);

            var declarationDomain = new Declaration(declarationId, taxpayerId, 2025, DeclarationStatus.EDITING, null);
            declarationDomain.addIncome(new Income("Some Company", IncomeType.SALARY, new BigDecimal("1000")));

            when(declarationRepository.findById(declarationId)).thenReturn(Optional.of(declarationEntity));
            when(declarationMapper.toDomain(any(DeclarationEntity.class))).thenReturn(declarationDomain);
            when(declarationRepository.save(any(DeclarationEntity.class))).thenAnswer(i -> i.getArgument(0));

            // When
            var result = declarationService.submitDeclaration(declarationId, taxpayerId);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(DeclarationStatus.DELIVERED);
            assertThat(result.getDeliveryDate()).isNotNull();
            verify(declarationRepository).save(any(DeclarationEntity.class));
        }

        @Test
        @DisplayName("When taxpayer tries to submit declaration without incomes Then exception should be thrown")
        void shouldRejectSubmissionWhenDeclarationHasNoIncomes() {
            // Given
            var declarationId = 1L;
            var taxpayerId = UUID.randomUUID();
            var declarationEntity = new DeclarationEntity();
            declarationEntity.setId(declarationId);
            declarationEntity.setTaxpayerId(taxpayerId);
            declarationEntity.setStatus(DeclarationStatus.EDITING);

            var declarationDomain = new Declaration(declarationId, taxpayerId, 2025, DeclarationStatus.EDITING, null);

            when(declarationRepository.findById(declarationId)).thenReturn(Optional.of(declarationEntity));
            when(declarationMapper.toDomain(any(DeclarationEntity.class))).thenReturn(declarationDomain);

            // When & Then
            assertThatThrownBy(() -> declarationService.submitDeclaration(declarationId, taxpayerId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Cannot submit a declaration with no incomes. Please report your incomes.");

            verify(declarationRepository, never()).save(any());
        }
    }
}