package br.com.matheusgusmao.incometax.service;

import br.com.matheusgusmao.incometax.domain.model.declaration.Declaration;
import br.com.matheusgusmao.incometax.domain.model.declaration.DeclarationStatus;
import br.com.matheusgusmao.incometax.domain.model.declaration.TaxCalculationResult;
import br.com.matheusgusmao.incometax.domain.model.dependent.Cpf;
import br.com.matheusgusmao.incometax.domain.model.dependent.Dependent;
import br.com.matheusgusmao.incometax.domain.model.expense.DeductibleExpense;
import br.com.matheusgusmao.incometax.domain.model.expense.ExpenseType;
import br.com.matheusgusmao.incometax.domain.model.income.Income;
import br.com.matheusgusmao.incometax.domain.model.income.IncomeType;
import br.com.matheusgusmao.incometax.domain.service.DeclarationService;
import br.com.matheusgusmao.incometax.domain.service.TaxCalculationService;
import br.com.matheusgusmao.incometax.infra.exception.custom.EntityAlreadyExistsException;
import br.com.matheusgusmao.incometax.infra.persistence.entity.declaration.DeclarationEntity;
import br.com.matheusgusmao.incometax.infra.persistence.entity.dependent.DependentEntity;
import br.com.matheusgusmao.incometax.infra.persistence.entity.expense.DeductibleExpenseEntity;
import br.com.matheusgusmao.incometax.infra.persistence.entity.income.IncomeEntity;
import br.com.matheusgusmao.incometax.infra.persistence.mapper.DeclarationMapper;
import br.com.matheusgusmao.incometax.infra.persistence.mapper.DeductibleExpenseMapper;
import br.com.matheusgusmao.incometax.infra.persistence.mapper.DependentMapper;
import br.com.matheusgusmao.incometax.infra.persistence.mapper.IncomeMapper;
import br.com.matheusgusmao.incometax.infra.persistence.repository.DeclarationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

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
    @InjectMocks
    private DeclarationService declarationService;
    @InjectMocks
    private TaxCalculationService taxCalculationService;

    @BeforeEach
    void setUp() {
        DeclarationMapper declarationMapper = new DeclarationMapper();
        ReflectionTestUtils.setField(declarationMapper, "dependentMapper", dependentMapper);
        ReflectionTestUtils.setField(declarationMapper, "incomeMapper", incomeMapper);
        ReflectionTestUtils.setField(declarationMapper, "deductibleExpenseMapper", deductibleExpenseMapper);
        ReflectionTestUtils.setField(declarationService, "declarationMapper", declarationMapper);
    }

    @Nested
    @DisplayName("Given a taxpayer wants to create a new declaration")
    @Tag("Unit")
    class CreateDeclarationTests {

        private UUID taxpayerId;
        private int year;

        @BeforeEach
        void setUp() {
            taxpayerId = UUID.randomUUID();
            year = 2025;
        }

        @Test
        @DisplayName("When taxpayer creates declaration for a new year Then declaration should be created successfully")
        void shouldCreateNewDeclarationSuccessfullyWhenNonExistent() {
            when(declarationRepository.existsByTaxpayerIdAndYear(taxpayerId, year)).thenReturn(false);
            when(declarationRepository.save(any(DeclarationEntity.class))).thenAnswer(invocation -> {
                DeclarationEntity entityToSave = invocation.getArgument(0);
                entityToSave.setId(1L);
                return entityToSave;
            });

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
            when(declarationRepository.existsByTaxpayerIdAndYear(taxpayerId, year)).thenReturn(true);

            assertThatThrownBy(() -> declarationService.createNewDeclaration(taxpayerId, year))
                    .isInstanceOf(EntityAlreadyExistsException.class);

            verify(declarationRepository, never()).save(any(DeclarationEntity.class));
        }
    }

    @Nested
    @DisplayName("Given a taxpayer wants to manage incomes in their declaration")
    @Tag("Unit")
    class IncomeManagementTests {

        private Long declarationId;
        private DeclarationEntity existingDeclarationEntity;

        @BeforeEach
        void setUp() {
            declarationId = 1L;
            UUID taxpayerId = UUID.randomUUID();

            existingDeclarationEntity = new DeclarationEntity();
            existingDeclarationEntity.setId(declarationId);
            existingDeclarationEntity.setTaxpayerId(taxpayerId);
            existingDeclarationEntity.setStatus(DeclarationStatus.EDITING);
            existingDeclarationEntity.setYear(2025);
        }

        @Test
        @DisplayName("When taxpayer adds valid income Then income should be added successfully")
        void shouldAddValidIncomeToDeclaration() {
            var newIncome = new Income("Company A", IncomeType.SALARY, new BigDecimal("50000.00"));

            when(declarationRepository.findById(declarationId)).thenReturn(Optional.of(existingDeclarationEntity));
            when(declarationRepository.save(any(DeclarationEntity.class))).thenAnswer(i -> i.getArgument(0));

            when(incomeMapper.toEntity(any(Income.class), any(DeclarationEntity.class))).thenAnswer(i -> {
                Income domain = i.getArgument(0);
                IncomeEntity entity = new IncomeEntity();
                entity.setPayingSource(domain.getPayingSource());
                entity.setType(domain.getType());
                entity.setValue(domain.getValue());
                return entity;
            });

            var updatedDeclaration = declarationService.addIncome(declarationId, newIncome);

            assertThat(updatedDeclaration).isNotNull();
            assertThat(updatedDeclaration.getIncomes()).hasSize(1);

            verify(declarationRepository).findById(declarationId);
            verify(declarationRepository).save(any(DeclarationEntity.class));
        }

        @Test
        @DisplayName("When taxpayer tries to add income with negative value Then exception should be thrown")
        void shouldPreventAddingIncomeWithNegativeValue() {
            assertThatThrownBy(() -> new Income("Company B", IncomeType.OTHER, new BigDecimal("-100.00")))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Income value cannot be negative.");

            verify(declarationRepository, never()).findById(any());
            verify(declarationRepository, never()).save(any());
        }

        @Test
        @DisplayName("When taxpayer adds multiple incomes from same source Then all incomes should be added")
        void shouldAllowDifferentIncomesFromSameSource() {
            var income1 = new Income("Company C", IncomeType.SALARY, new BigDecimal("60000.00"));
            var income2 = new Income("Company C", IncomeType.VACATION, new BigDecimal("5000.00"));

            var incomeEntity1 = new IncomeEntity();
            incomeEntity1.setPayingSource(income1.getPayingSource());
            incomeEntity1.setType(income1.getType());
            incomeEntity1.setValue(income1.getValue());

            var declarationEntityAfterFirstSave = new DeclarationEntity();
            declarationEntityAfterFirstSave.setId(declarationId);
            declarationEntityAfterFirstSave.setTaxpayerId(existingDeclarationEntity.getTaxpayerId());
            declarationEntityAfterFirstSave.setStatus(DeclarationStatus.EDITING);
            declarationEntityAfterFirstSave.setYear(2025);
            declarationEntityAfterFirstSave.setIncomes(List.of(incomeEntity1));

            when(declarationRepository.findById(declarationId))
                    .thenReturn(Optional.of(existingDeclarationEntity))
                    .thenReturn(Optional.of(declarationEntityAfterFirstSave));

            when(incomeMapper.toDomain(any(IncomeEntity.class))).thenAnswer(i -> {
                IncomeEntity entity = i.getArgument(0);
                return new Income(entity.getId(), entity.getPayingSource(), entity.getType(), entity.getValue());
            });
            when(incomeMapper.toEntity(any(Income.class), any(DeclarationEntity.class))).thenAnswer(i -> {
                Income domain = i.getArgument(0);
                IncomeEntity entity = new IncomeEntity();
                entity.setPayingSource(domain.getPayingSource());
                entity.setType(domain.getType());
                entity.setValue(domain.getValue());
                return entity;
            });

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
            var incomeIdToRemove = 10L;

            var incomeEntity = new IncomeEntity();
            incomeEntity.setId(incomeIdToRemove);
            existingDeclarationEntity.setIncomes(List.of(incomeEntity));

            var incomeDomain = new Income(incomeIdToRemove, "Company A", IncomeType.SALARY, new BigDecimal("50000"));
            when(incomeMapper.toDomain(any(IncomeEntity.class))).thenReturn(incomeDomain);
            when(declarationRepository.findById(declarationId)).thenReturn(Optional.of(existingDeclarationEntity));
            when(declarationRepository.save(any(DeclarationEntity.class))).thenAnswer(i -> i.getArgument(0));

            var updatedDeclaration = declarationService.removeIncome(declarationId, incomeIdToRemove);

            assertThat(updatedDeclaration).isNotNull();
            assertThat(updatedDeclaration.getIncomes()).isEmpty();
            verify(declarationRepository).save(any(DeclarationEntity.class));
        }

        @Test
        @DisplayName("When taxpayer tries to remove non-existent income Then exception should be thrown")
        void shouldThrowExceptionWhenRemovingNonExistentIncome() {
            var nonExistentIncomeId = 99L;
            when(declarationRepository.findById(declarationId)).thenReturn(Optional.of(existingDeclarationEntity));

            assertThatThrownBy(() -> declarationService.removeIncome(declarationId, nonExistentIncomeId))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Income not found with id: " + nonExistentIncomeId);

            verify(declarationRepository, never()).save(any(DeclarationEntity.class));
        }
    }

    @Nested
    @DisplayName("Given a taxpayer wants to manage deductible expenses in their declaration")
    @Tag("Unit")
    class DeductibleExpenseManagementTests {

        private Long declarationId;
        private DeclarationEntity existingDeclarationEntity;

        @BeforeEach
        void setUp() {
            declarationId = 1L;
            existingDeclarationEntity = new DeclarationEntity();
            existingDeclarationEntity.setId(declarationId);
            existingDeclarationEntity.setStatus(DeclarationStatus.EDITING);
        }

        @Test
        @DisplayName("When taxpayer adds valid health expense Then expense should be added successfully")
        void shouldAddDeductibleHealthExpenseSuccessfully() {
            var expense = new DeductibleExpense("Consulta médica", ExpenseType.HEALTH, new BigDecimal("350.00"));
            when(declarationRepository.findById(declarationId)).thenReturn(Optional.of(existingDeclarationEntity));
            when(declarationRepository.save(any(DeclarationEntity.class))).thenAnswer(i -> i.getArgument(0));

            var result = declarationService.addDeductibleExpense(declarationId, expense);

            assertThat(result).isNotNull();
            assertThat(result.getDeductibleExpenses()).hasSize(1);
        }

        @Test
        @DisplayName("When taxpayer adds non-deductible expense Then expense should be added successfully")
        void shouldAcceptNonDeductibleExpense() {
            var expense = new DeductibleExpense("Assinatura de revista", ExpenseType.OTHER, new BigDecimal("50.00"));
            when(declarationRepository.findById(declarationId)).thenReturn(Optional.of(existingDeclarationEntity));
            when(declarationRepository.save(any(DeclarationEntity.class))).thenAnswer(i -> i.getArgument(0));

            var result = declarationService.addDeductibleExpense(declarationId, expense);

            assertThat(result).isNotNull();
            assertThat(result.getDeductibleExpenses()).hasSize(1);
        }

        @Test
        @DisplayName("When taxpayer removes existing expense Then expense should be removed successfully")
        void shouldRemoveDeductibleExpenseSuccessfully() {
            var expenseIdToRemove = 20L;

            var expenseEntity = new DeductibleExpenseEntity();
            expenseEntity.setId(expenseIdToRemove);
            existingDeclarationEntity.setDeductibleExpenses(List.of(expenseEntity));

            var expenseDomain = new DeductibleExpense(expenseIdToRemove, "Plano de Saúde", ExpenseType.HEALTH, new BigDecimal("600"));
            when(deductibleExpenseMapper.toDomain(any(DeductibleExpenseEntity.class))).thenReturn(expenseDomain);

            when(declarationRepository.findById(declarationId)).thenReturn(Optional.of(existingDeclarationEntity));
            when(declarationRepository.save(any(DeclarationEntity.class))).thenAnswer(i -> i.getArgument(0));

            var updatedDeclaration = declarationService.removeDeductibleExpense(declarationId, expenseIdToRemove);

            assertThat(updatedDeclaration).isNotNull();
            assertThat(updatedDeclaration.getDeductibleExpenses()).isEmpty();
        }

        @Test
        @DisplayName("When taxpayer tries to add expense with zero or negative value Then exception should be thrown")
        void shouldRejectExpenseWithZeroValue() {
            assertThatThrownBy(() -> new DeductibleExpense("Inscrição", ExpenseType.EDUCATION, BigDecimal.ZERO))
                    .isInstanceOf(IllegalArgumentException.class);

            assertThatThrownBy(() -> new DeductibleExpense("Inscrição", ExpenseType.EDUCATION, new BigDecimal("-100")))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("Given a taxpayer wants to view their declaration history")
    @Tag("Unit")
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
    @Tag("Unit")
    class DeclarationSubmissionTests {

        private Long declarationId;
        private UUID taxpayerId;
        private DeclarationEntity declarationEntity;

        @BeforeEach
        void setUp() {
            declarationId = 1L;
            taxpayerId = UUID.randomUUID();

            declarationEntity = new DeclarationEntity();
            declarationEntity.setId(declarationId);
            declarationEntity.setTaxpayerId(taxpayerId);
            declarationEntity.setStatus(DeclarationStatus.EDITING);

            when(declarationRepository.findById(declarationId)).thenReturn(Optional.of(declarationEntity));
        }

        @Test
        @DisplayName("When taxpayer submits complete declaration Then declaration should be submitted successfully")
        void shouldSubmitDeclarationSuccessfullyWhenComplete() {
            var incomeEntity = new IncomeEntity();
            incomeEntity.setValue(new BigDecimal("1000"));
            declarationEntity.setIncomes(List.of(incomeEntity));

            var incomeDomain = new Income("Some Company", IncomeType.SALARY, new BigDecimal("1000"));
            when(incomeMapper.toDomain(any(IncomeEntity.class))).thenReturn(incomeDomain);
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
            assertThatThrownBy(() -> declarationService.submitDeclaration(declarationId, taxpayerId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Cannot submit a declaration with no incomes. Please report your incomes.");

            verify(declarationRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Given a taxpayer wants to manage dependents in their declaration")
    @Tag("Unit")
    class DependentManagementTests {

        private Long declarationId;
        private DeclarationEntity existingDeclarationEntity;

        @BeforeEach
        void setUp() {
            declarationId = 1L;
            UUID taxpayerId = UUID.randomUUID();
            existingDeclarationEntity = new DeclarationEntity();
            existingDeclarationEntity.setId(declarationId);
            existingDeclarationEntity.setTaxpayerId(taxpayerId);
            existingDeclarationEntity.setYear(2025);
            existingDeclarationEntity.setStatus(DeclarationStatus.EDITING);
        }

        @Test
        @DisplayName("When taxpayer adds a valid dependent Then dependent should be added successfully")
        void shouldAddValidDependentToDeclaration() {
            var newDependent = new Dependent("Maria Silva", new Cpf("753.838.240-22"), LocalDate.of(2010, 5, 15));

            when(declarationRepository.findById(declarationId)).thenReturn(Optional.of(existingDeclarationEntity));
            when(declarationRepository.save(any(DeclarationEntity.class))).thenAnswer(i -> i.getArgument(0));

            Declaration updatedDeclaration = declarationService.addDependent(declarationId, newDependent);

            assertThat(updatedDeclaration).isNotNull();
            assertThat(updatedDeclaration.getDependents()).hasSize(1);

            verify(declarationRepository).findById(declarationId);
            verify(declarationRepository).save(any(DeclarationEntity.class));
        }

        @Test
        @DisplayName("When taxpayer removes an existing dependent Then dependent should be removed successfully")
        void shouldRemoveDependentSuccessfully() {
            var dependentIdToRemove = 10L;

            var existingDependentEntity = new DependentEntity();
            existingDependentEntity.setId(dependentIdToRemove);
            existingDependentEntity.setName("Carlos Pereira");
            existingDependentEntity.setCpf("75383824022"); // CPF sem máscara
            existingDependentEntity.setBirthDate(LocalDate.of(2015, 3, 20));
            existingDeclarationEntity.setDependents(List.of(existingDependentEntity));

            when(declarationRepository.findById(declarationId)).thenReturn(Optional.of(existingDeclarationEntity));

            var dependentDomain = new Dependent(dependentIdToRemove, "Carlos Pereira", new Cpf("753.838.240-22"), LocalDate.of(2015, 3, 20));
            when(dependentMapper.toDomain(any(DependentEntity.class))).thenReturn(dependentDomain);

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
            when(declarationRepository.findById(declarationId)).thenReturn(Optional.of(existingDeclarationEntity));

            assertThatThrownBy(() -> declarationService.removeDependent(declarationId, nonExistentDependentId))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Dependent not found with id: " + nonExistentDependentId);

            verify(declarationRepository, never()).save(any(DeclarationEntity.class));
        }
    }

    @Nested
    @DisplayName("Calculate Tax Due")
    @Tag("Unit")
    class TaxCalculationServiceTest {
        private Long declarationId;

        @BeforeEach
        void setUp() {
            declarationId = 1L;
            DeclarationMapper realDeclarationMapper = new DeclarationMapper();
            ReflectionTestUtils.setField(realDeclarationMapper, "incomeMapper", incomeMapper);
            ReflectionTestUtils.setField(realDeclarationMapper, "deductibleExpenseMapper", deductibleExpenseMapper);
            ReflectionTestUtils.setField(taxCalculationService, "declarationMapper", realDeclarationMapper);
        }

        @Test
        @DisplayName("Given a declaration has no income, when the calculation is requested, then the system should return zero")
        void shouldReturnZeroWhenDeclarationHasNoIncome() {
            var emptyEntity = new DeclarationEntity();
            emptyEntity.setId(declarationId);
            when(declarationRepository.findById(declarationId)).thenReturn(Optional.of(emptyEntity));

            TaxCalculationResult result = taxCalculationService.calculate(declarationId);

            assertThat(result).isNotNull();
            assertThat(result.taxDue()).isZero();
        }

        @Test
        @DisplayName("Given a declaration with income and deductions, when the calculation is requested, then the progressive table is applied")
        void shouldApplyProgressiveTableWhenDeclarationHasIncomeAndDeductions() {
            var incomeEntity = new IncomeEntity();
            incomeEntity.setValue(new BigDecimal("60000.00"));

            var expenseEntity = new DeductibleExpenseEntity();
            expenseEntity.setValue(new BigDecimal("5000.00"));

            var declarationEntity = new DeclarationEntity();
            declarationEntity.setId(declarationId);
            declarationEntity.setIncomes(List.of(incomeEntity));
            declarationEntity.setDeductibleExpenses(List.of(expenseEntity));
            declarationEntity.setStatus(DeclarationStatus.EDITING);

            when(declarationRepository.findById(declarationId)).thenReturn(Optional.of(declarationEntity));

            when(incomeMapper.toDomain(any(IncomeEntity.class)))
                    .thenReturn(new Income("Dummy Source", IncomeType.SALARY, new BigDecimal("60000.00")));

            when(deductibleExpenseMapper.toDomain(any(DeductibleExpenseEntity.class)))
                    .thenReturn(new DeductibleExpense("Dummy Expense", ExpenseType.HEALTH, new BigDecimal("5000.00")));

            TaxCalculationResult result = taxCalculationService.calculate(declarationId);

            assertThat(result.totalIncome()).isEqualByComparingTo("60000.00");
            assertThat(result.totalDeductions()).isEqualByComparingTo("5000.00");
            assertThat(result.calculationBase()).isEqualByComparingTo("55000.00");
            assertThat(result.taxDue()).isEqualByComparingTo("4421.76");
        }
    }
}