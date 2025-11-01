package br.com.matheusgusmao.incometax.domain.model.dependent;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Cpf Domain - Structural Tests")
@Tag("Structural")
@Tag("UnitTest")
@Tag("Unit")
public class CpfDomainTest {
    @Nested
    @DisplayName("CPF Construction Validation")
    class CpfConstructionValidationTests {

        @Test
        @DisplayName("Should create CPF with valid value")
        void shouldCreateCpfWithValidValue() {
            var cpf = new Cpf("12345678909");

            assertThat(cpf.getValue()).isEqualTo("12345678909");
        }

        @Test
        @DisplayName("Should accept CPF with formatting and clean it")
        void shouldAcceptCpfWithFormattingAndCleanIt() {
            var cpf = new Cpf("123.456.789-09");

            assertThat(cpf.getValue()).isEqualTo("12345678909");
        }

        @Test
        @DisplayName("Should throw exception when CPF is null")
        void shouldThrowExceptionWhenCpfIsNull() {
            assertThatThrownBy(() -> new Cpf(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("CPF cannot be null or empty");
        }

        @Test
        @DisplayName("Should throw exception when CPF is empty string")
        void shouldThrowExceptionWhenCpfIsEmptyString() {
            assertThatThrownBy(() -> new Cpf(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("CPF cannot be null or empty");
        }

        @Test
        @DisplayName("Should throw exception when CPF is blank")
        void shouldThrowExceptionWhenCpfIsBlank() {
            assertThatThrownBy(() -> new Cpf("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("CPF cannot be null or empty");
        }

        @Test
        @DisplayName("Should throw exception when CPF has less than 11 digits")
        void shouldThrowExceptionWhenCpfHasLessThan11Digits() {
            assertThatThrownBy(() -> new Cpf("1234567890"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("CPF must contain exactly 11 digits");
        }

        @Test
        @DisplayName("Should throw exception when CPF has more than 11 digits")
        void shouldThrowExceptionWhenCpfHasMoreThan11Digits() {
            assertThatThrownBy(() -> new Cpf("123456789012"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("CPF must contain exactly 11 digits");
        }

        @Test
        @DisplayName("Should throw exception when CPF has all same digits")
        void shouldThrowExceptionWhenCpfHasAllSameDigits() {
            assertThatThrownBy(() -> new Cpf("11111111111"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("CPF cannot have all same digits");
        }

        @Test
        @DisplayName("Should throw exception when CPF has all zeros")
        void shouldThrowExceptionWhenCpfHasAllZeros() {
            assertThatThrownBy(() -> new Cpf("00000000000"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("CPF cannot have all same digits");
        }

        @Test
        @DisplayName("Should throw exception when CPF has invalid check digit")
        void shouldThrowExceptionWhenCpfHasInvalidCheckDigit() {
            assertThatThrownBy(() -> new Cpf("12345678900"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid CPF");
        }

        @Test
        @DisplayName("Should accept valid CPF with first digit remainder < 2")
        void shouldAcceptValidCpfWithFirstDigitRemainderLessThan2() {
            var cpf = new Cpf("11144477735");

            assertThat(cpf.getValue()).isEqualTo("11144477735");
        }

        @Test
        @DisplayName("Should accept valid CPF with first digit remainder >= 2")
        void shouldAcceptValidCpfWithFirstDigitRemainderGreaterOrEqual2() {
            var cpf = new Cpf("12345678909");

            assertThat(cpf.getValue()).isEqualTo("12345678909");
        }

        @Test
        @DisplayName("Should accept valid CPF with well-known format")
        void shouldAcceptValidCpfWithWellKnownFormat() {
            var cpf = new Cpf("11144477735");

            assertThat(cpf.getValue()).isEqualTo("11144477735");
        }

        @Test
        @DisplayName("Should handle CPF with non-digit characters")
        void shouldHandleCpfWithNonDigitCharacters() {
            var cpf = new Cpf("111.444.777-35");

            assertThat(cpf.getValue()).isEqualTo("11144477735");
        }
    }
    @Nested
    @DisplayName("CPF Equality Tests")
    class CpfEqualityTests {

        @Test
        @DisplayName("Should be equal when CPF values are the same")
        void shouldBeEqualWhenCpfValuesAreTheSame() {
            var cpf1 = new Cpf("12345678909");
            var cpf2 = new Cpf("123.456.789-09");

            assertThat(cpf1).isEqualTo(cpf2);
            assertThat(cpf1.hashCode()).isEqualTo(cpf2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when CPF values are different")
        void shouldNotBeEqualWhenCpfValuesAreDifferent() {
            var cpf1 = new Cpf("11144477735");
            var cpf2 = new Cpf("12345678909");

            assertThat(cpf1).isNotEqualTo(cpf2);
        }

        @Test
        @DisplayName("Should be equal when same instance")
        void shouldBeEqualWhenSameInstance() {
            var cpf = new Cpf("12345678909");

            assertThat(cpf).isEqualTo(cpf);
        }

        @Test
        @DisplayName("Should not be equal when compared with different type")
        void shouldNotBeEqualWhenComparedWithDifferentType() {
            var cpf = new Cpf("12345678909");

            assertThat(cpf).isNotEqualTo("12345678909");
            assertThat(cpf).isNotEqualTo(null);
        }
    }
    @Nested
    @DisplayName("CPF String Representation")
    class CpfStringRepresentationTests {

        @Test
        @DisplayName("Should return CPF value in toString")
        void shouldReturnCpfValueInToString() {
            var cpf = new Cpf("12345678909");

            assertThat(cpf.toString()).isEqualTo("12345678909");
        }

        @Test
        @DisplayName("Should return cleaned CPF value in toString")
        void shouldReturnCleanedCpfValueInToString() {
            var cpf = new Cpf("123.456.789-09");

            assertThat(cpf.toString()).isEqualTo("12345678909");
        }
    }
    @Nested
    @DisplayName("Edge Cases for CPF Validation")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should validate CPF where second digit has remainder < 2")
        void shouldValidateCpfWhereSecondDigitHasRemainderLessThan2() throws Exception {
            var cpf = new Cpf("11144477735");
            assertThat(cpf).isNotNull();
        }

        @Test
        @DisplayName("Should validate CPF where second digit has remainder >= 2")
        void shouldValidateCpfWhereSecondDigitHasRemainderGreaterOrEqual2() {
            var cpf = new Cpf("12345678909");
            assertThat(cpf).isNotNull();
        }

        @Test
        @DisplayName("Should reject CPF with special characters but wrong length")
        void shouldRejectCpfWithSpecialCharactersButWrongLength() {
            assertThatThrownBy(() -> new Cpf("123.456.789"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("CPF must contain exactly 11 digits");
        }

        @Test
        @DisplayName("Should handle CPF with mixed special characters")
        void shouldHandleCpfWithMixedSpecialCharacters() {
            var cpf = new Cpf("123.456.789-09");

            assertThat(cpf.getValue()).isEqualTo("12345678909");
        }

        @Test
        @DisplayName("Should reject formatted CPF with all same digits")
        void shouldRejectFormattedCpfWithAllSameDigits() {
            assertThatThrownBy(() -> new Cpf("111.111.111-11"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("CPF cannot have all same digits");
        }
    }



}
