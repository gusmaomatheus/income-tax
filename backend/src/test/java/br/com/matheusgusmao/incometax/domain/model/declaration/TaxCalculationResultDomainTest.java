package br.com.matheusgusmao.incometax.domain.model.declaration;

import br.com.matheusgusmao.incometax.domain.service.TaxCalculationService;
import br.com.matheusgusmao.incometax.infra.persistence.entity.declaration.DeclarationEntity;
import br.com.matheusgusmao.incometax.infra.persistence.mapper.DeclarationMapper;
import br.com.matheusgusmao.incometax.infra.persistence.repository.DeclarationRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("TaxCalculationResult Domain - Structural Tests")
@Tag("Structural")
@Tag("UnitTest")
@Tag("Unit")
public class TaxCalculationResultDomainTest {
    @Nested
    @DisplayName("TaxCalculationResult Construction")
    class TaxCalculationResultConstructionTests {

        @Test
        @DisplayName("Should create TaxCalculationResult with 5 parameters")
        void shouldCreateTaxCalculationResultWith5Parameters() {
            var totalIncome = BigDecimal.valueOf(10000);
            var totalDeductions = BigDecimal.valueOf(2000);
            var calculationBase = BigDecimal.valueOf(8000);
            var taxDue = BigDecimal.valueOf(1200);
            var effectiveAliquot = BigDecimal.valueOf(15.0);

            var result = new TaxCalculationResult(totalIncome, totalDeductions, calculationBase, taxDue, effectiveAliquot);

            assertThat(result.totalIncome()).isEqualByComparingTo(totalIncome);
            assertThat(result.totalDeductions()).isEqualByComparingTo(totalDeductions);
            assertThat(result.calculationBase()).isEqualByComparingTo(calculationBase);
            assertThat(result.taxDue()).isEqualByComparingTo(taxDue);
            assertThat(result.effectiveAliquot()).isEqualByComparingTo(effectiveAliquot);
            assertThat(result.finalBalance()).isEqualByComparingTo(taxDue);
        }

        @Test
        @DisplayName("Should create TaxCalculationResult with 6 parameters")
        void shouldCreateTaxCalculationResultWith6Parameters() {
            var totalIncome = BigDecimal.valueOf(10000);
            var totalDeductions = BigDecimal.valueOf(2000);
            var calculationBase = BigDecimal.valueOf(8000);
            var taxDue = BigDecimal.valueOf(1200);
            var effectiveAliquot = BigDecimal.valueOf(15.0);
            var finalBalance = BigDecimal.valueOf(500);

            var result = new TaxCalculationResult(totalIncome, totalDeductions, calculationBase, taxDue, effectiveAliquot, finalBalance);

            assertThat(result.totalIncome()).isEqualByComparingTo(totalIncome);
            assertThat(result.totalDeductions()).isEqualByComparingTo(totalDeductions);
            assertThat(result.calculationBase()).isEqualByComparingTo(calculationBase);
            assertThat(result.taxDue()).isEqualByComparingTo(taxDue);
            assertThat(result.effectiveAliquot()).isEqualByComparingTo(effectiveAliquot);
            assertThat(result.finalBalance()).isEqualByComparingTo(finalBalance);
        }

        @Test
        @DisplayName("Should handle zero values in 5 parameter constructor")
        void shouldHandleZeroValuesIn5ParameterConstructor() {
            var result = new TaxCalculationResult(
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO
            );

            assertThat(result.totalIncome()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(result.totalDeductions()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(result.finalBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Should handle zero values in 6 parameter constructor")
        void shouldHandleZeroValuesIn6ParameterConstructor() {
            var result = new TaxCalculationResult(
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO
            );

            assertThat(result.totalIncome()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(result.finalBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Should handle negative finalBalance in 6 parameter constructor")
        void shouldHandleNegativeFinalBalanceIn6ParameterConstructor() {
            var result = new TaxCalculationResult(
                    BigDecimal.valueOf(10000),
                    BigDecimal.valueOf(2000),
                    BigDecimal.valueOf(8000),
                    BigDecimal.valueOf(1200),
                    BigDecimal.valueOf(15.0),
                    BigDecimal.valueOf(-500) // Negative balance (refund)
            );

            assertThat(result.finalBalance()).isEqualByComparingTo(BigDecimal.valueOf(-500));
        }

        @Test
        @DisplayName("Should set finalBalance equal to taxDue in 5 parameter constructor")
        void shouldSetFinalBalanceEqualToTaxDueIn5ParameterConstructor() {
            var taxDue = BigDecimal.valueOf(1500);
            var result = new TaxCalculationResult(
                    BigDecimal.valueOf(10000),
                    BigDecimal.valueOf(2000),
                    BigDecimal.valueOf(8000),
                    taxDue,
                    BigDecimal.valueOf(18.75)
            );

            assertThat(result.finalBalance()).isEqualByComparingTo(taxDue);
        }

        @Test
        @DisplayName("Should handle large values correctly")
        void shouldHandleLargeValuesCorrectly() {
            var result = new TaxCalculationResult(
                    new BigDecimal("999999.99"),
                    new BigDecimal("50000.00"),
                    new BigDecimal("949999.99"),
                    new BigDecimal("142500.00"),
                    new BigDecimal("15.0")
            );

            assertThat(result.totalIncome()).isEqualByComparingTo(new BigDecimal("999999.99"));
            assertThat(result.finalBalance()).isEqualByComparingTo(new BigDecimal("142500.00"));
        }

        @Test
        @DisplayName("Should handle decimal precision correctly")
        void shouldHandleDecimalPrecisionCorrectly() {
            var result = new TaxCalculationResult(
                    new BigDecimal("1234.56"),
                    new BigDecimal("123.45"),
                    new BigDecimal("1111.11"),
                    new BigDecimal("166.67"),
                    new BigDecimal("15.0001"),
                    new BigDecimal("166.67")
            );

            assertThat(result.totalIncome()).isEqualByComparingTo(new BigDecimal("1234.56"));
            assertThat(result.effectiveAliquot()).isEqualByComparingTo(new BigDecimal("15.0001"));
        }
    }
}

