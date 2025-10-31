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
}