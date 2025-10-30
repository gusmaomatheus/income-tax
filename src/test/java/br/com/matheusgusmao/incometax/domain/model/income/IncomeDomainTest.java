package br.com.matheusgusmao.incometax.domain.model.income;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Income Domain - Structural Tests")
@Tag("Structural")
@Tag("UnitTest")
@Tag("Unit")
public class IncomeDomainTest {
    @Nested
    @DisplayName("Income Construction")
    class IncomeConstructionTests {

        @Test
        @DisplayName("Should create income with valid parameters")
        void shouldCreateIncomeWithValidParameters() {
            var payingSource = "Company XYZ";
            var type = IncomeType.SALARY;
            var value = BigDecimal.valueOf(5000);

            var income = new Income(payingSource, type, value);

            assertThat(income.getPayingSource()).isEqualTo(payingSource);
            assertThat(income.getType()).isEqualTo(type);
            assertThat(income.getValue()).isEqualTo(value);
            assertThat(income.getId()).isNull();
        }

        @Test
        @DisplayName("Should create income with id")
        void shouldCreateIncomeWithId() {
            var id = 1L;
            var payingSource = "Company XYZ";
            var type = IncomeType.SALARY;
            var value = BigDecimal.valueOf(5000);

            var income = new Income(id, payingSource, type, value);

            assertThat(income.getId()).isEqualTo(id);
            assertThat(income.getPayingSource()).isEqualTo(payingSource);
            assertThat(income.getType()).isEqualTo(type);
            assertThat(income.getValue()).isEqualTo(value);
        }

        @Test
        @DisplayName("Should accept zero value")
        void shouldAcceptZeroValue() {
            var income = new Income("Company XYZ", IncomeType.SALARY, BigDecimal.ZERO);

            assertThat(income.getValue()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Should accept all income types")
        void shouldAcceptAllIncomeTypes() {
            for (IncomeType type : IncomeType.values()) {
                var income = new Income("Source", type, BigDecimal.valueOf(1000));
                assertThat(income.getType()).isEqualTo(type);
            }
        }

    }

    @Nested
    @DisplayName("Income Validation - Value")
    class IncomeValueValidationTests {

        @Test
        @DisplayName("Should throw exception when value is null")
        void shouldThrowExceptionWhenValueIsNull() {
            assertThatThrownBy(() -> new Income("Company XYZ", IncomeType.SALARY, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Income value cannot be negative");
        }

        @Test
        @DisplayName("Should throw exception when value is negative")
        void shouldThrowExceptionWhenValueIsNegative() {
            assertThatThrownBy(() -> new Income("Company X", IncomeType.SALARY, BigDecimal.valueOf(-100)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Income value cannot be negative");
        }

        @Test
        @DisplayName("Should throw exception when value is negative in constructor with id")
        void shouldThrowExceptionWhenValueIsNegativeInConstructorWithId() {
            assertThatThrownBy(() -> new Income(1L, "Company Y", IncomeType.SALARY, BigDecimal.valueOf(-100)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Income value cannot be negative");
        }

        @Test
        @DisplayName("Should accept positive value")
        void shouldAcceptPositiveValue() {
            var income = new Income("Company P", IncomeType.SALARY, BigDecimal.valueOf(100));

            assertThat(income.getValue()).isPositive();
        }

        @Test
        @DisplayName("Should accept large decimal values")
        void shouldAcceptLargeDecimalValues() {
            var income = new Income("Company X", IncomeType.SALARY, new BigDecimal("999999.99"));

            assertThat(income.getValue()).isEqualByComparingTo(new BigDecimal("999999.99"));
        }
    }
    @Nested
    @DisplayName("Income Validation - Paying Source")
    class IncomePayingSourceValidationTests {

        @Test
        @DisplayName("Should throw exception when payingSource is null")
        void shouldThrowExceptionWhenPayingSourceIsNull() {
            assertThatThrownBy(() -> new Income(null, IncomeType.SALARY, BigDecimal.valueOf(1000)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Paying source cannot be empty");
        }
        @Test
        @DisplayName("Should throw exception when payingSource is blank")
        void shouldThrowExceptionWhenPayingSourceIsBlank() {
            assertThatThrownBy(() -> new Income("   ", IncomeType.SALARY, BigDecimal.valueOf(1000)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Paying source cannot be empty");
        }

        @Test
        @DisplayName("Should throw exception when payingSource is empty string")
        void shouldThrowExceptionWhenPayingSourceIsEmptyString() {
            assertThatThrownBy(() -> new Income("", IncomeType.SALARY, BigDecimal.valueOf(1000)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Paying source cannot be empty");
        }
        @Test
        @DisplayName("Should accept non-blank payingSource")
        void shouldAcceptNonBlankPayingSource() {
            var income = new Income("Valid Name", IncomeType.SALARY, BigDecimal.valueOf(1000));

            assertThat(income.getPayingSource()).isEqualTo("Valid Name");
        }
        @Test
        @DisplayName("Should accept payingSource with special characters")
        void shouldAcceptPayingSourceWithSpecialCharacters() {
            var income = new Income("Company & Co. Ltda.", IncomeType.SALARY, BigDecimal.valueOf(1000));

            assertThat(income.getPayingSource()).isEqualTo("Company & Co. Ltda.");
        }
    }
}