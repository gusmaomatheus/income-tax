package br.com.matheusgusmao.incometax.domain.model;
import br.com.matheusgusmao.incometax.domain.model.income.Income;
import br.com.matheusgusmao.incometax.domain.model.income.IncomeType;
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
}