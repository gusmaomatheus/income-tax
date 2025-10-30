package br.com.matheusgusmao.incometax.domain.model.expense;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("DeductibleExpense Domain - Structural Tests")
@Tag("Structural")
@Tag("UnitTest")
@Tag("Unit")
public class DeductibleExpenseDomainTest {
    @Nested
    @DisplayName("DeductibleExpense Construction")
    class DeductibleExpenseConstructionTests {

        @Test
        @DisplayName("Should create expense with valid parameters")
        void shouldCreateExpenseWithValidParameters() {
            var description = "Health Plan";
            var type = ExpenseType.HEALTH;
            var value = BigDecimal.valueOf(1200);

            var expense = new DeductibleExpense(description, type, value);

            assertThat(expense.getDescription()).isEqualTo(description);
            assertThat(expense.getType()).isEqualTo(type);
            assertThat(expense.getValue()).isEqualTo(value);
            assertThat(expense.getId()).isNull();
        }
        @Test
        @DisplayName("Should create expense with id")
        void shouldCreateExpenseWithId() {
            var id = 1L;
            var description = "Health Plan";
            var type = ExpenseType.HEALTH;
            var value = BigDecimal.valueOf(1200);

            var expense = new DeductibleExpense(id, description, type, value);

            assertThat(expense.getId()).isEqualTo(id);
            assertThat(expense.getDescription()).isEqualTo(description);
            assertThat(expense.getType()).isEqualTo(type);
            assertThat(expense.getValue()).isEqualTo(value);
        }
        @Test
        @DisplayName("Should accept all expense types")
        void shouldAcceptAllExpenseTypes() {
            for (ExpenseType type : ExpenseType.values()) {
                var expense = new DeductibleExpense("Description", type, BigDecimal.valueOf(100));
                assertThat(expense.getType()).isEqualTo(type);
            }
        }
    }
    @Nested
    @DisplayName("DeductibleExpense Validation - Value")
    class DeductibleExpenseValueValidationTests {

        @Test
        @DisplayName("Should throw exception when value is null")
        void shouldThrowExceptionWhenValueIsNull() {
            assertThatThrownBy(() -> new DeductibleExpense("Health Plan", ExpenseType.HEALTH, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Expense value must be positive");
        }
        @Test
        @DisplayName("Should throw exception when value is zero")
        void shouldThrowExceptionWhenValueIsZero() {
            assertThatThrownBy(() -> new DeductibleExpense("Health Plan", ExpenseType.HEALTH, BigDecimal.ZERO))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Expense value must be positive");
        }
        @Test
        @DisplayName("Should throw exception when value is negative")
        void shouldThrowExceptionWhenValueIsNegative() {
            assertThatThrownBy(() -> new DeductibleExpense("Health Plan", ExpenseType.HEALTH, BigDecimal.valueOf(-100)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Expense value must be positive");
        }
        @Test
        @DisplayName("Should throw exception when value is negative in constructor with id")
        void shouldThrowExceptionWhenValueIsNegativeInConstructorWithId() {
            assertThatThrownBy(() -> new DeductibleExpense(1L, "Health Plan", ExpenseType.HEALTH, BigDecimal.valueOf(-100)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Expense value must be positive");
        }
        @Test
        @DisplayName("Should throw exception when value is zero in constructor with id")
        void shouldThrowExceptionWhenValueIsZeroInConstructorWithId() {
            assertThatThrownBy(() -> new DeductibleExpense(1L, "Health Plan", ExpenseType.HEALTH, BigDecimal.ZERO))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Expense value must be positive");
        }
        @Test
        @DisplayName("Should accept positive value")
        void shouldAcceptPositiveValue() {
            var expense = new DeductibleExpense("Health Plan", ExpenseType.HEALTH, BigDecimal.valueOf(100));

            assertThat(expense.getValue()).isPositive();
        }
        @Test
        @DisplayName("Should accept small positive value")
        void shouldAcceptSmallPositiveValue() {
            var expense = new DeductibleExpense("Expense", ExpenseType.OTHER, new BigDecimal("0.01"));

            assertThat(expense.getValue()).isEqualByComparingTo(new BigDecimal("0.01"));
        }
        @Test
        @DisplayName("Should accept large decimal values")
        void shouldAcceptLargeDecimalValues() {
            var expense = new DeductibleExpense("Expense", ExpenseType.HEALTH, new BigDecimal("999999.99"));

            assertThat(expense.getValue()).isEqualByComparingTo(new BigDecimal("999999.99"));
        }
    }
    @Nested
    @DisplayName("DeductibleExpense Validation - Description")
    class DeductibleExpenseDescriptionValidationTests {

        @Test
        @DisplayName("Should throw exception when description is null")
        void shouldThrowExceptionWhenDescriptionIsNull() {
            assertThatThrownBy(() -> new DeductibleExpense(null, ExpenseType.HEALTH, BigDecimal.valueOf(100)))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Description cannot be null");
        }

        @Test
        @DisplayName("Should throw exception when description is null in constructor with id")
        void shouldThrowExceptionWhenDescriptionIsNullInConstructorWithId() {
            assertThatThrownBy(() -> new DeductibleExpense(1L, null, ExpenseType.HEALTH, BigDecimal.valueOf(100)))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Description cannot be null");
        }
        @Test
        @DisplayName("Should accept blank description")
        void shouldAcceptBlankDescription() {
            var expense = new DeductibleExpense("   ", ExpenseType.HEALTH, BigDecimal.valueOf(100));

            assertThat(expense.getDescription()).isEqualTo("   ");
        }
        @Test
        @DisplayName("Should accept empty string description")
        void shouldAcceptEmptyStringDescription() {
            var expense = new DeductibleExpense("", ExpenseType.HEALTH, BigDecimal.valueOf(100));

            assertThat(expense.getDescription()).isEqualTo("");
        }
        @Test
        @DisplayName("Should accept valid description")
        void shouldAcceptValidDescription() {
            var expense = new DeductibleExpense("Valid Description", ExpenseType.HEALTH, BigDecimal.valueOf(100));

            assertThat(expense.getDescription()).isEqualTo("Valid Description");
        }
    }
    @Nested
    @DisplayName("DeductibleExpense Validation - Type")
    class DeductibleExpenseTypeValidationTests {

        @Test
        @DisplayName("Should throw exception when type is null")
        void shouldThrowExceptionWhenTypeIsNull() {
            assertThatThrownBy(() -> new DeductibleExpense("Health Plan", null, BigDecimal.valueOf(100)))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Type cannot be null");
        }
    }
}
