package br.com.matheusgusmao.incometax.service;

import br.com.matheusgusmao.incometax.domain.model.declaration.Declaration;
import br.com.matheusgusmao.incometax.domain.model.declaration.DeclarationStatus;
import br.com.matheusgusmao.incometax.domain.model.dependent.Cpf;
import br.com.matheusgusmao.incometax.domain.model.dependent.Dependent;
import br.com.matheusgusmao.incometax.domain.model.expense.DeductibleExpense;
import br.com.matheusgusmao.incometax.domain.model.expense.ExpenseType;
import br.com.matheusgusmao.incometax.domain.model.income.Income;
import br.com.matheusgusmao.incometax.domain.model.income.IncomeType;
import br.com.matheusgusmao.incometax.domain.service.DeclarationService;
import br.com.matheusgusmao.incometax.infra.exception.custom.EntityAlreadyExistsException;
import br.com.matheusgusmao.incometax.infra.persistence.entity.declaration.DeclarationEntity;
import br.com.matheusgusmao.incometax.infra.persistence.mapper.DeclarationMapper;
import br.com.matheusgusmao.incometax.infra.persistence.mapper.DeductibleExpenseMapper;
import br.com.matheusgusmao.incometax.infra.persistence.mapper.DependentMapper;
import br.com.matheusgusmao.incometax.infra.persistence.mapper.IncomeMapper;
import br.com.matheusgusmao.incometax.infra.persistence.repository.DeclarationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
            var taxpayerId = UUID.randomUUID();
            var year = 2025;

            when(declarationRepository.existsByTaxpayerIdAndYear(taxpayerId, year)).thenReturn(false);
            when(declarationRepository.save(any(DeclarationEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

            var result = declarationService.createNewDeclaration(taxpayerId, year);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            verify(declarationRepository).existsByTaxpayerIdAndYear(taxpayerId, year);
            verify(declarationRepository).save(any(DeclarationEntity.class));
        }

        @Test
        @DisplayName("When taxpayer tries to create declaration for existing year Then exception should be thrown")
        void shouldThrowExceptionWhenDeclarationForSameYearAlreadyExists() {
            var taxpayerId = UUID.randomUUID();
            var year = 2025;
            when(declarationRepository.existsByTaxpayerIdAndYear(taxpayerId, year)).thenReturn(true);

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
            var declarationId = 1L;
            var newIncome = new Income("Company A", IncomeType.SALARY, new BigDecimal("50000.00"));

            var existingDeclarationEntity = new DeclarationEntity();
            existingDeclarationEntity.setId(declarationId);
            existingDeclarationEntity.setStatus(DeclarationStatus.EDITING);
            existingDeclarationEntity.setTaxpayerId(UUID.randomUUID());
            existingDeclarationEntity.setYear(2025);

            when(declarationRepository.findById(declarationId)).thenReturn(Optional.of(existingDeclarationEntity));
            when(declarationRepository.save(any(DeclarationEntity.class))).thenAnswer(i -> i.getArgument(0));

            var updatedDeclaration = declarationService.addIncome(declarationId, newIncome);

            assertThat(updatedDeclaration).isNotNull();
            assertThat(updatedDeclaration.getIncomes()).hasSize(1);
            assertThat(updatedDeclaration.getIncomes().getFirst().getPayingSource()).isEqualTo("Company A");
            verify(declarationRepository).findById(declarationId);
            verify(declarationRepository).save(any(DeclarationEntity.class));
        }

        @Test
        @DisplayName("When taxpayer tries to add income with negative value Then exception should be thrown")
        void shouldPreventAddingIncomeWithNegativeValue() {
            var declarationId = 1L;

            assertThatThrownBy(() -> new Income("Company B", IncomeType.OTHER, new BigDecimal("-100.00")))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Income value cannot be negative.");

            verify(declarationRepository, never()).findById(any());
            verify(declarationRepository, never()).save(any());
        }

        @Test
        @DisplayName("When taxpayer adds multiple incomes from same source Then all incomes should be added")
        void shouldAllowDifferentIncomesFromSameSource() {
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

            declarationService.addIncome(declarationId, income1);
            var updatedDeclaration = declarationService.addIncome(declarationId, income2);

            assertThat(updatedDeclaration).isNotNull();
            assertThat(updatedDeclaration.getIncomes()).hasSize(2);
            verify(declarationRepository, times(2)).findById(declarationId);
            verify(declarationRepository, times(2)).save(any(DeclarationEntity.class));
        }

        @Test
        @DisplayName("When taxpayer removes existing income Then income should be removed successfully")
        void shouldRemoveIncomeSuccessfully() {
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

            var updatedDeclaration = declarationService.removeIncome(declarationId, incomeIdToRemove);

            assertThat(updatedDeclaration).isNotNull();
            assertThat(updatedDeclaration.getIncomes()).isEmpty();
            assertThat(updatedDeclaration.getIncomes()).noneMatch(i -> i.getId().equals(incomeIdToRemove));
            verify(declarationRepository).findById(declarationId);
            verify(declarationRepository).save(any(DeclarationEntity.class));
        }

        @Test
        @DisplayName("When taxpayer tries to remove non-existent income Then exception should be thrown")
        void shouldThrowExceptionWhenRemovingNonExistentIncome() {
            var declarationId = 1L;
            var nonExistentIncomeId = 99L;

            var existingDeclarationEntity = new DeclarationEntity();
            existingDeclarationEntity.setId(declarationId);
            existingDeclarationEntity.setStatus(DeclarationStatus.EDITING);

            var declarationDomain = new Declaration(declarationId, UUID.randomUUID(), 2025, DeclarationStatus.EDITING, null);

            when(declarationRepository.findById(declarationId)).thenReturn(Optional.of(existingDeclarationEntity));
            when(declarationMapper.toDomain(any(DeclarationEntity.class))).thenReturn(declarationDomain);

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
            var declarationId = 1L;
            var existingDeclarationEntity = new DeclarationEntity();
            existingDeclarationEntity.setId(declarationId);

            when(declarationRepository.findById(declarationId)).thenReturn(Optional.of(existingDeclarationEntity));
            when(declarationRepository.save(any(DeclarationEntity.class))).thenAnswer(i -> i.getArgument(0));

            var expense = new DeductibleExpense("Consulta médica", ExpenseType.HEALTH, new BigDecimal("350.00"));

            var result = declarationService.addDeductibleExpense(declarationId, expense);

            assertThat(result).isNotNull();
            assertThat(result.getDeductibleExpenses()).hasSize(1);
            assertThat(result.getDeductibleExpenses()).first().usingRecursiveComparison().isEqualTo(expense);
            verify(declarationRepository).findById(declarationId);
            verify(declarationRepository).save(any(DeclarationEntity.class));
        }

        @Test
        @DisplayName("When taxpayer tries to add expense with zero or negative value Then exception should be thrown")
        void shouldRejectExpenseWithZeroValue() {
            assertThatThrownBy(() -> new DeductibleExpense("Inscrição", ExpenseType.EDUCATION, BigDecimal.ZERO))
                    .isInstanceOf(IllegalArgumentException.class);

            assertThatThrownBy(() -> new DeductibleExpense("Inscrição", ExpenseType.EDUCATION, new BigDecimal("-100")))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("When taxpayer adds non-deductible expense Then expense should be added successfully")
        void shouldAcceptNonDeductibleExpense() {
            var declarationId = 1L;
            var existingDeclarationEntity = new DeclarationEntity();
            existingDeclarationEntity.setId(declarationId);

            when(declarationRepository.findById(declarationId)).thenReturn(Optional.of(existingDeclarationEntity));
            when(declarationRepository.save(any(DeclarationEntity.class))).thenAnswer(i -> i.getArgument(0));

            var expense = new DeductibleExpense("Assinatura de revista", ExpenseType.OTHER, new BigDecimal("50.00"));

            var result = declarationService.addDeductibleExpense(declarationId, expense);

            assertThat(result).isNotNull();
            assertThat(result.getDeductibleExpenses()).hasSize(1);
            assertThat(result.getDeductibleExpenses().getFirst().getType()).isEqualTo(ExpenseType.OTHER);
        }

        @Test
        @DisplayName("When taxpayer removes existing expense Then expense should be removed successfully")
        void shouldRemoveDeductibleExpenseSuccessfully() {
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

            var updatedDeclaration = declarationService.removeDeductibleExpense(declarationId, expenseIdToRemove);

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

            var result = declarationService.getDeclarationHistory(taxpayerId);

            assertThat(result).hasSize(2);
            assertThat(result.get(0).year()).isEqualTo(2022);
            assertThat(result.get(0).status()).isEqualTo("EDITING");
            assertThat(result.get(1).year()).isEqualTo(2023);
            assertThat(result.get(1).status()).isEqualTo("EDITING");
        }

        @Test
        @DisplayName("When taxpayer has no declarations Then empty list should be returned")
        void shouldReturnEmptyListWhenNoDeclarationsFound() {
            var taxpayerId = UUID.randomUUID();
            when(declarationRepository.findAllByTaxpayerId(taxpayerId)).thenReturn(List.of());

            var result = declarationService.getDeclarationHistory(taxpayerId);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Given a taxpayer wants to submit their declaration")
    class DeclarationSubmissionTests {

        @Test
        @DisplayName("When taxpayer submits complete declaration Then declaration should be submitted successfully")
        void shouldSubmitDeclarationSuccessfullyWhenComplete() {
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

            var result = declarationService.submitDeclaration(declarationId, taxpayerId);

            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(DeclarationStatus.DELIVERED);
            assertThat(result.getDeliveryDate()).isNotNull();
            verify(declarationRepository).save(any(DeclarationEntity.class));
        }

        @Test
        @DisplayName("When taxpayer tries to submit declaration without incomes Then exception should be thrown")
        void shouldRejectSubmissionWhenDeclarationHasNoIncomes() {
            var declarationId = 1L;
            var taxpayerId = UUID.randomUUID();
            var declarationEntity = new DeclarationEntity();
            declarationEntity.setId(declarationId);
            declarationEntity.setTaxpayerId(taxpayerId);
            declarationEntity.setStatus(DeclarationStatus.EDITING);

            var declarationDomain = new Declaration(declarationId, taxpayerId, 2025, DeclarationStatus.EDITING, null);

            when(declarationRepository.findById(declarationId)).thenReturn(Optional.of(declarationEntity));
            when(declarationMapper.toDomain(any(DeclarationEntity.class))).thenReturn(declarationDomain);

            assertThatThrownBy(() -> declarationService.submitDeclaration(declarationId, taxpayerId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Cannot submit a declaration with no incomes. Please report your incomes.");

            verify(declarationRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Given a taxpayer wants to manage dependents in their declaration")
    class DependentManagementTests {

        private Long declarationId;
        private UUID taxpayerId;
        private DeclarationEntity existingDeclarationEntity;

        @BeforeEach
        void setUp() {
            declarationId = 1L;
            taxpayerId = UUID.randomUUID();

            existingDeclarationEntity = new DeclarationEntity();
            existingDeclarationEntity.setId(declarationId);
            existingDeclarationEntity.setTaxpayerId(taxpayerId);
            existingDeclarationEntity.setYear(2025);
            existingDeclarationEntity.setStatus(DeclarationStatus.EDITING);
        }

        @Test
        @DisplayName("When taxpayer adds a valid dependent Then dependent should be added successfully")
        void shouldAddValidDependentToDeclaration() {
            var newDependent = new Dependent("Maria Silva", new Cpf("123.456.789-00"), LocalDate.of(2010, 5, 15));

            when(declarationRepository.findById(declarationId)).thenReturn(Optional.of(existingDeclarationEntity));
            when(declarationRepository.save(any(DeclarationEntity.class))).thenAnswer(i -> i.getArgument(0));

            Declaration updatedDeclaration = declarationService.addDependent(declarationId, newDependent);

            assertThat(updatedDeclaration).isNotNull();
            assertThat(updatedDeclaration.getDependents()).hasSize(1);
            assertThat(updatedDeclaration.getDependents().getFirst().getName()).isEqualTo("Maria Silva");
            assertThat(updatedDeclaration.getDependents().getFirst().getCpf().getValue()).isEqualTo("12345678900");

            verify(declarationRepository).findById(declarationId);
            verify(declarationRepository).save(any(DeclarationEntity.class));
        }

        @Test
        @DisplayName("When taxpayer tries to add a dependent that already exists Then exception should be thrown")
        void shouldPreventAddingDuplicateDependent() {
            var cpf = new Cpf("111.222.333-44");
            var dependent1 = new Dependent(1L, "Joao Souza", cpf, LocalDate.of(2012, 1, 1));
            var dependent2 = new Dependent(2L, "Joao S.", cpf, LocalDate.of(2012, 1, 1));

            var declarationDomain = new Declaration(declarationId, taxpayerId, 2025, DeclarationStatus.EDITING, null);
            declarationDomain.addDependent(dependent1);

            when(declarationRepository.findById(declarationId)).thenReturn(Optional.of(existingDeclarationEntity));
            when(declarationMapper.toDomain(any(DeclarationEntity.class))).thenReturn(declarationDomain);

            assertThatThrownBy(() -> declarationService.addDependent(declarationId, dependent2))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("A dependent with the same CPF already exists in this declaration.");

            verify(declarationRepository).findById(declarationId);
            verify(declarationRepository, never()).save(any(DeclarationEntity.class));
        }

        @Test
        @DisplayName("When taxpayer removes an existing dependent Then dependent should be removed successfully")
        void shouldRemoveDependentSuccessfully() {
            var dependentIdToRemove = 10L;
            var dependent = new Dependent(dependentIdToRemove, "Carlos Pereira", new Cpf("987.654.321-00"), LocalDate.of(2015, 3, 20));

            var declarationDomain = new Declaration(declarationId, taxpayerId, 2025, DeclarationStatus.EDITING, null);
            declarationDomain.addDependent(dependent);

            when(declarationRepository.findById(declarationId)).thenReturn(Optional.of(existingDeclarationEntity));
            when(declarationMapper.toDomain(any(DeclarationEntity.class))).thenReturn(declarationDomain);
            when(declarationRepository.save(any(DeclarationEntity.class))).thenAnswer(i -> i.getArgument(0));

            Declaration updatedDeclaration = declarationService.removeDependent(declarationId, dependentIdToRemove);

            assertThat(updatedDeclaration).isNotNull();
            assertThat(updatedDeclaration.getDependents()).isEmpty();
            assertThat(updatedDeclaration.getDependents()).noneMatch(d -> d.getId().equals(dependentIdToRemove));

            verify(declarationRepository).findById(declarationId);
            verify(declarationRepository).save(any(DeclarationEntity.class));
        }

        @Test
        @DisplayName("When taxpayer tries to remove a non-existent dependent Then exception should be thrown")
        void shouldThrowExceptionWhenRemovingNonExistentDependent() {
            var nonExistentDependentId = 99L;
            var declarationDomain = new Declaration(declarationId, taxpayerId, 2025, DeclarationStatus.EDITING, null);

            when(declarationRepository.findById(declarationId)).thenReturn(Optional.of(existingDeclarationEntity));
            when(declarationMapper.toDomain(any(DeclarationEntity.class))).thenReturn(declarationDomain);

            assertThatThrownBy(() -> declarationService.removeDependent(declarationId, nonExistentDependentId))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Dependent not found with id: " + nonExistentDependentId);

            verify(declarationRepository, never()).save(any(DeclarationEntity.class));
        }
    }
}