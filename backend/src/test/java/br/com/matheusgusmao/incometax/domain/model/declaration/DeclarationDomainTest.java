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


}