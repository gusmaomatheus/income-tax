package br.com.matheusgusmao.incometax.domain.model.declaration;

import br.com.matheusgusmao.incometax.domain.model.dependent.Cpf;
import br.com.matheusgusmao.incometax.domain.model.dependent.Dependent;
import br.com.matheusgusmao.incometax.domain.model.expense.DeductibleExpense;
import br.com.matheusgusmao.incometax.domain.model.expense.ExpenseType;
import br.com.matheusgusmao.incometax.domain.model.income.Income;
import br.com.matheusgusmao.incometax.domain.model.income.IncomeType;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Declaration Domain - Structural Tests")
@Tag("Structural")
@Tag("UnitTest")
@Tag("Unit")
class DeclarationDomainTest {

    private UUID taxpayerId = UUID.randomUUID();

    @Nested
    @DisplayName("Declaration Construction")
    class DeclarationConstructionTests {

        @Test
        @DisplayName("Should create declaration with valid taxpayerId and year")
        void shouldCreateDeclarationWithValidInputs() {
            var declaration = new Declaration(taxpayerId, 2024);

            assertThat(declaration.getTaxpayerId()).isEqualTo(taxpayerId);
            assertThat(declaration.getYear()).isEqualTo(2024);
            assertThat(declaration.getStatus()).isEqualTo(DeclarationStatus.EDITING);
            assertThat(declaration.getId()).isNull();
        }

        @Test
        @DisplayName("Should throw exception when taxpayerId is null")
        void shouldThrowExceptionWhenTaxpayerIdIsNull() {
            assertThatThrownBy(() -> new Declaration(null, 2024))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Taxpayer ID cannot be null");
        }

        @Test
        @DisplayName("Should throw exception when year has invalid format")
        void shouldThrowExceptionWhenYearHasInvalidFormat() {
            assertThatThrownBy(() -> new Declaration(taxpayerId, 123))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid year format");
        }

        @Test
        @DisplayName("Should create declaration with all parameters")
        void shouldCreateDeclarationWithAllParameters() {
            var id = 1L;
            var status = DeclarationStatus.DELIVERED;
            var deliveryDate = LocalDateTime.now();

            var declaration = new Declaration(id, taxpayerId, 2024, status, deliveryDate);

            assertThat(declaration.getId()).isEqualTo(id);
            assertThat(declaration.getStatus()).isEqualTo(status);
            assertThat(declaration.getDeliveryDate()).isEqualTo(deliveryDate);
        }
    }
    @Nested
    @DisplayName("Income Management")
    class IncomeManagementTests {

        @Test
        @DisplayName("Should add income when declaration is in editing status")
        void shouldAddIncomeWhenDeclarationIsInEditingStatus() {
            var declaration = new Declaration(taxpayerId, 2024);
            var income = new Income("Company A", IncomeType.SALARY, BigDecimal.valueOf(5000));

            declaration.addIncome(income);

            assertThat(declaration.getIncomes()).hasSize(1);
            assertThat(declaration.getIncomes().get(0)).isEqualTo(income);
        }

        @Test
        @DisplayName("Should throw exception when adding income to submitted declaration")
        void shouldThrowExceptionWhenAddingIncomeToSubmittedDeclaration() {
            var declaration = new Declaration(1L, taxpayerId, 2024, DeclarationStatus.DELIVERED, LocalDateTime.now());
            var income = new Income("Company A", IncomeType.SALARY, BigDecimal.valueOf(5000));

            assertThatThrownBy(() -> declaration.addIncome(income))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot add income to a declaration that is not in editing status");
        }

        @Test
        @DisplayName("Should remove income when declaration is in editing status")
        void shouldRemoveIncomeWhenDeclarationIsInEditingStatus() {
            var declaration = new Declaration(taxpayerId, 2024);
            var income = new Income(1L, "Company A", IncomeType.SALARY, BigDecimal.valueOf(5000));
            declaration.addIncome(income);

            declaration.removeIncome(1L);

            assertThat(declaration.getIncomes()).isEmpty();
        }

        @Test
        @DisplayName("Should throw exception when removing income from submitted declaration")
        void shouldThrowExceptionWhenRemovingIncomeFromSubmittedDeclaration() {
            var declaration = new Declaration(1L, taxpayerId, 2024, DeclarationStatus.DELIVERED, LocalDateTime.now());

            assertThatThrownBy(() -> declaration.removeIncome(1L))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot remove income from a declaration that is not in editing status");
        }

        @Test
        @DisplayName("Should throw exception when removing non-existent income")
        void shouldThrowExceptionWhenRemovingNonExistentIncome() {
            var declaration = new Declaration(taxpayerId, 2024);

            assertThatThrownBy(() -> declaration.removeIncome(999L))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Income not found with id: 999");
        }
    }
    @Nested
    @DisplayName("Expense Management")
    class ExpenseManagementTests {

        @Test
        @DisplayName("Should add expense when declaration is in editing status")
        void shouldAddExpenseWhenDeclarationIsInEditingStatus() {
            var declaration = new Declaration(taxpayerId, 2024);
            var expense = new DeductibleExpense("Health Plan", ExpenseType.HEALTH, BigDecimal.valueOf(1200));

            declaration.addDeductibleExpense(expense);

            assertThat(declaration.getDeductibleExpenses()).hasSize(1);
            assertThat(declaration.getDeductibleExpenses().get(0)).isEqualTo(expense);
        }

        @Test
        @DisplayName("Should throw exception when adding expense to submitted declaration")
        void shouldThrowExceptionWhenAddingExpenseToSubmittedDeclaration() {
            var declaration = new Declaration(1L, taxpayerId, 2024, DeclarationStatus.DELIVERED, LocalDateTime.now());
            var expense = new DeductibleExpense("Health Plan", ExpenseType.HEALTH, BigDecimal.valueOf(1200));

            assertThatThrownBy(() -> declaration.addDeductibleExpense(expense))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot add expense to a declaration that is not in editing status");
        }

        @Test
        @DisplayName("Should remove expense when declaration is in editing status")
        void shouldRemoveExpenseWhenDeclarationIsInEditingStatus() {
            var declaration = new Declaration(taxpayerId, 2024);
            var expense = new DeductibleExpense(1L, "Health Plan", ExpenseType.HEALTH, BigDecimal.valueOf(1200));
            declaration.addDeductibleExpense(expense);

            declaration.removeDeductibleExpense(1L);

            assertThat(declaration.getDeductibleExpenses()).isEmpty();
        }

        @Test
        @DisplayName("Should throw exception when removing expense from submitted declaration")
        void shouldThrowExceptionWhenRemovingExpenseFromSubmittedDeclaration() {
            var declaration = new Declaration(1L, taxpayerId, 2024, DeclarationStatus.DELIVERED, LocalDateTime.now());

            assertThatThrownBy(() -> declaration.removeDeductibleExpense(1L))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot remove expense from a declaration that is not in editing status");
        }

        @Test
        @DisplayName("Should throw exception when removing non-existent expense")
        void shouldThrowExceptionWhenRemovingNonExistentExpense() {
            var declaration = new Declaration(taxpayerId, 2024);

            assertThatThrownBy(() -> declaration.removeDeductibleExpense(999L))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Deductible expense not found with id: 999");
        }
    }
    @Nested
    @DisplayName("Dependent Management")
    class DependentManagementTests {

        @Test
        @DisplayName("Should add dependent when declaration is in editing status")
        void shouldAddDependentWhenDeclarationIsInEditingStatus() throws Exception {
            var declaration = new Declaration(taxpayerId, 2024);
            var cpf = new Cpf("12345678909");
            var dependent = new Dependent("John Doe", cpf, LocalDate.of(2010, 1, 1));

            declaration.addDependent(dependent);

            assertThat(declaration.getDependents()).hasSize(1);
            assertThat(declaration.getDependents().get(0)).isEqualTo(dependent);
        }

        @Test
        @DisplayName("Should throw exception when adding dependent to submitted declaration")
        void shouldThrowExceptionWhenAddingDependentToSubmittedDeclaration() throws Exception {
            var declaration = new Declaration(1L, taxpayerId, 2024, DeclarationStatus.DELIVERED, LocalDateTime.now());
            var cpf = new Cpf("12345678909");
            var dependent = new Dependent("John Doe", cpf, LocalDate.of(2010, 1, 1));

            assertThatThrownBy(() -> declaration.addDependent(dependent))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot add dependent to a declaration that is not in editing status");
        }

        @Test
        @DisplayName("Should remove dependent when declaration is in editing status")
        void shouldRemoveDependentWhenDeclarationIsInEditingStatus() throws Exception {
            var declaration = new Declaration(taxpayerId, 2024);
            var cpf = new Cpf("12345678909");
            var dependent = new Dependent(1L, "John Doe", cpf, LocalDate.of(2010, 1, 1));
            declaration.addDependent(dependent);

            declaration.removeDependent(1L);

            assertThat(declaration.getDependents()).isEmpty();
        }

        @Test
        @DisplayName("Should throw exception when removing dependent from submitted declaration")
        void shouldThrowExceptionWhenRemovingDependentFromSubmittedDeclaration() {
            var declaration = new Declaration(1L, taxpayerId, 2024, DeclarationStatus.DELIVERED, LocalDateTime.now());

            assertThatThrownBy(() -> declaration.removeDependent(1L))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot remove dependent from a declaration that is not in editing status");
        }

        @Test
        @DisplayName("Should throw exception when removing non-existent dependent")
        void shouldThrowExceptionWhenRemovingNonExistentDependent() {
            var declaration = new Declaration(taxpayerId, 2024);

            assertThatThrownBy(() -> declaration.removeDependent(999L))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Dependent not found with id: 999");
        }
    }
    @Nested
    @DisplayName("Declaration Submission")
    class DeclarationSubmissionTests {

        @Test
        @DisplayName("Should submit declaration when it has incomes")
        void shouldSubmitDeclarationWhenItHasIncomes() {
            var declaration = new Declaration(taxpayerId, 2024);
            var income = new Income("Company A", IncomeType.SALARY, BigDecimal.valueOf(5000));
            declaration.addIncome(income);

            declaration.submit();

            assertThat(declaration.getStatus()).isEqualTo(DeclarationStatus.DELIVERED);
            assertThat(declaration.getDeliveryDate()).isNotNull();
        }

        @Test
        @DisplayName("Should throw exception when submitting declaration without incomes")
        void shouldThrowExceptionWhenSubmittingDeclarationWithoutIncomes() {
            var declaration = new Declaration(taxpayerId, 2024);

            assertThatThrownBy(() -> declaration.submit())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot submit a declaration with no incomes");
        }

        @Test
        @DisplayName("Should throw exception when submitting already submitted declaration")
        void shouldThrowExceptionWhenSubmittingAlreadySubmittedDeclaration() {
            var declaration = new Declaration(1L, taxpayerId, 2024, DeclarationStatus.DELIVERED, LocalDateTime.now());

            assertThatThrownBy(() -> declaration.submit())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Declaration can only be submitted if it's in editing status");
        }
    }
    @Nested
    @DisplayName("Calculation Methods")
    class CalculationMethodsTests {

        @Test
        @DisplayName("Should calculate total income correctly")
        void shouldCalculateTotalIncomeCorrectly() {
            var declaration = new Declaration(taxpayerId, 2024);
            declaration.addIncome(new Income("Company A", IncomeType.SALARY, BigDecimal.valueOf(5000)));
            declaration.addIncome(new Income("Company B", IncomeType.SALARY, BigDecimal.valueOf(3000)));

            var total = declaration.calculateTotalIncome();

            assertThat(total).isEqualByComparingTo(BigDecimal.valueOf(8000));
        }

        @Test
        @DisplayName("Should return zero when there are no incomes")
        void shouldReturnZeroWhenThereAreNoIncomes() {
            var declaration = new Declaration(taxpayerId, 2024);

            var total = declaration.calculateTotalIncome();

            assertThat(total).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Should calculate total deductions correctly")
        void shouldCalculateTotalDeductionsCorrectly() {
            var declaration = new Declaration(taxpayerId, 2024);
            declaration.addDeductibleExpense(new DeductibleExpense("Health", ExpenseType.HEALTH, BigDecimal.valueOf(1200)));
            declaration.addDeductibleExpense(new DeductibleExpense("Education", ExpenseType.EDUCATION, BigDecimal.valueOf(800)));

            var total = declaration.calculateTotalDeductions();

            assertThat(total).isEqualByComparingTo(BigDecimal.valueOf(2000));
        }

        @Test
        @DisplayName("Should return zero when there are no deductible expenses")
        void shouldReturnZeroWhenThereAreNoDeductibleExpenses() {
            var declaration = new Declaration(taxpayerId, 2024);

            var total = declaration.calculateTotalDeductions();

            assertThat(total).isEqualByComparingTo(BigDecimal.ZERO);
        }
    }
    @Tag("Mutation")
    @Nested
    @DisplayName("Declaration Edge Cases and Mutant Coverage")
    class DeclarationEdgeCasesTests {

        @Test
        @DisplayName("Should throw exception when year has invalid format - 2 digits")
        void shouldThrowExceptionWhenYearHas2Digits() {
            assertThatThrownBy(() -> new Declaration(taxpayerId, 24))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid year format");
        }

        @Test
        @DisplayName("Should throw exception when year has invalid format - 5 digits")
        void shouldThrowExceptionWhenYearHas5Digits() {
            assertThatThrownBy(() -> new Declaration(taxpayerId, 20245))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid year format");
        }

        @Test
        @DisplayName("Should remove correct income when multiple incomes exist")
        void shouldRemoveCorrectIncomeWhenMultipleIncomesExist() {
            var declaration = new Declaration(taxpayerId, 2024);
            var income1 = new Income(1L, "Company A", IncomeType.SALARY, BigDecimal.valueOf(5000));
            var income2 = new Income(2L, "Company B", IncomeType.SALARY, BigDecimal.valueOf(3000));
            var income3 = new Income(3L, "Company C", IncomeType.SALARY, BigDecimal.valueOf(2000));
            declaration.addIncome(income1);
            declaration.addIncome(income2);
            declaration.addIncome(income3);

            declaration.removeIncome(2L);

            assertThat(declaration.getIncomes()).hasSize(2);
            assertThat(declaration.getIncomes()).containsExactly(income1, income3);
            assertThat(declaration.getIncomes()).doesNotContain(income2);
        }

        @Test
        @DisplayName("Should remove correct expense when multiple expenses exist")
        void shouldRemoveCorrectExpenseWhenMultipleExpensesExist() {
            var declaration = new Declaration(taxpayerId, 2024);
            var expense1 = new DeductibleExpense(1L, "Health", ExpenseType.HEALTH, BigDecimal.valueOf(1200));
            var expense2 = new DeductibleExpense(2L, "Education", ExpenseType.EDUCATION, BigDecimal.valueOf(800));
            var expense3 = new DeductibleExpense(3L, "Other", ExpenseType.OTHER, BigDecimal.valueOf(500));
            declaration.addDeductibleExpense(expense1);
            declaration.addDeductibleExpense(expense2);
            declaration.addDeductibleExpense(expense3);

            declaration.removeDeductibleExpense(2L);

            assertThat(declaration.getDeductibleExpenses()).hasSize(2);
            assertThat(declaration.getDeductibleExpenses()).containsExactly(expense1, expense3);
            assertThat(declaration.getDeductibleExpenses()).doesNotContain(expense2);
        }

        @Test
        @DisplayName("Should remove correct dependent when multiple dependents exist")
        void shouldRemoveCorrectDependentWhenMultipleDependentsExist() throws Exception {
            var declaration = new Declaration(taxpayerId, 2024);
            var cpf1 = new Cpf("12345678909");
            var cpf2 = new Cpf("98765432100");
            var cpf3 = new Cpf("11144477735");
            var dependent1 = new Dependent(1L, "John", cpf1, LocalDate.of(2010, 1, 1));
            var dependent2 = new Dependent(2L, "Jane", cpf2, LocalDate.of(2012, 2, 2));
            var dependent3 = new Dependent(3L, "Bob", cpf3, LocalDate.of(2014, 3, 3));
            declaration.addDependent(dependent1);
            declaration.addDependent(dependent2);
            declaration.addDependent(dependent3);

            declaration.removeDependent(2L);

            assertThat(declaration.getDependents()).hasSize(2);
            assertThat(declaration.getDependents()).containsExactly(dependent1, dependent3);
            assertThat(declaration.getDependents()).doesNotContain(dependent2);
        }

        @Test
        @DisplayName("Should throw exception when removing income with null id")
        void shouldThrowExceptionWhenRemovingIncomeWithNullId() {
            var declaration = new Declaration(taxpayerId, 2024);
            var income = new Income("Company A", IncomeType.SALARY, BigDecimal.valueOf(5000));
            declaration.addIncome(income);

            assertThatThrownBy(() -> declaration.removeIncome(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should throw exception when removing expense with null id")
        void shouldThrowExceptionWhenRemovingExpenseWithNullId() {
            var declaration = new Declaration(taxpayerId, 2024);
            var expense = new DeductibleExpense("Health", ExpenseType.HEALTH, BigDecimal.valueOf(1200));
            declaration.addDeductibleExpense(expense);

            assertThatThrownBy(() -> declaration.removeDeductibleExpense(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should throw exception when removing dependent with null id")
        void shouldThrowExceptionWhenRemovingDependentWithNullId() throws Exception {
            var declaration = new Declaration(taxpayerId, 2024);
            var cpf = new Cpf("12345678909");
            var dependent = new Dependent("John", cpf, LocalDate.of(2010, 1, 1));
            declaration.addDependent(dependent);

            assertThatThrownBy(() -> declaration.removeDependent(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should return unmodifiable list for deductible expenses")
        void shouldReturnUnmodifiableListForDeductibleExpenses() {
            var declaration = new Declaration(taxpayerId, 2024);
            var expense = new DeductibleExpense("Health", ExpenseType.HEALTH, BigDecimal.valueOf(1200));
            declaration.addDeductibleExpense(expense);

            var expenses = declaration.getDeductibleExpenses();

            assertThatThrownBy(() -> expenses.add(new DeductibleExpense("Education", ExpenseType.EDUCATION, BigDecimal.valueOf(800))))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("Should return unmodifiable list for dependents")
        void shouldReturnUnmodifiableListForDependents() throws Exception {
            var declaration = new Declaration(taxpayerId, 2024);
            var cpf = new Cpf("12345678909");
            var dependent = new Dependent("John", cpf, LocalDate.of(2010, 1, 1));
            declaration.addDependent(dependent);

            var dependents = declaration.getDependents();

            assertThatThrownBy(() -> {
                var newCpf = new Cpf("98765432100");
                dependents.add(new Dependent("Jane", newCpf, LocalDate.of(2012, 2, 2)));
            }).isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("Should calculate total income with multiple incomes correctly")
        void shouldCalculateTotalIncomeWithMultipleIncomesCorrectly() {
            var declaration = new Declaration(taxpayerId, 2024);
            declaration.addIncome(new Income("Company A", IncomeType.SALARY, new BigDecimal("5000.50")));
            declaration.addIncome(new Income("Company B", IncomeType.VACATION, new BigDecimal("3000.25")));
            declaration.addIncome(new Income("Company C", IncomeType.THIRTEENTH_SALARY, new BigDecimal("2000.75")));

            var total = declaration.calculateTotalIncome();

            assertThat(total).isEqualByComparingTo(new BigDecimal("10001.50"));
        }

        @Test
        @DisplayName("Should calculate total deductions with multiple expenses correctly")
        void shouldCalculateTotalDeductionsWithMultipleExpensesCorrectly() {
            var declaration = new Declaration(taxpayerId, 2024);
            declaration.addDeductibleExpense(new DeductibleExpense("Health", ExpenseType.HEALTH, new BigDecimal("1200.50")));
            declaration.addDeductibleExpense(new DeductibleExpense("Education", ExpenseType.EDUCATION, new BigDecimal("800.25")));
            declaration.addDeductibleExpense(new DeductibleExpense("Other", ExpenseType.OTHER, new BigDecimal("500.75")));

            var total = declaration.calculateTotalDeductions();

            assertThat(total).isEqualByComparingTo(new BigDecimal("2501.50"));
        }

        @Test
        @DisplayName("Should handle year 1000")
        void shouldHandleYear1000() {
            var declaration = new Declaration(taxpayerId, 1000);
            assertThat(declaration.getYear()).isEqualTo(1000);
        }

        @Test
        @DisplayName("Should handle year 9999")
        void shouldHandleYear9999() {
            var declaration = new Declaration(taxpayerId, 9999);
            assertThat(declaration.getYear()).isEqualTo(9999);
        }

        @Test
        @DisplayName("Should calculate total income with zero values")
        void shouldCalculateTotalIncomeWithZeroValues() {
            var declaration = new Declaration(taxpayerId, 2024);
            declaration.addIncome(new Income("Company A", IncomeType.SALARY, BigDecimal.ZERO));
            declaration.addIncome(new Income("Company B", IncomeType.SALARY, BigDecimal.ZERO));

            var total = declaration.calculateTotalIncome();
            assertThat(total).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Should calculate total deductions with zero values")
        void shouldCalculateTotalDeductionsWithZeroValues() {
            var declaration = new Declaration(taxpayerId, 2024);
            declaration.addDeductibleExpense(new DeductibleExpense("Health", ExpenseType.HEALTH, new BigDecimal("0.01")));

            var total = declaration.calculateTotalDeductions();

            assertThat(total).isEqualByComparingTo(new BigDecimal("0.01"));
        }
    }

}