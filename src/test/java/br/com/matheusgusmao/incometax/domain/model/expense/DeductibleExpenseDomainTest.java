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
    }
}
