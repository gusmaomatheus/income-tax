package br.com.matheusgusmao.incometax.domain.model.dependent;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Dependent Domain - Structural Tests")
@Tag("Structural")
@Tag("UnitTest")
@Tag("Unit")
public class DependentDomainTest {
    @Nested
    @DisplayName("Dependent Construction")
    class DependentConstructionTests {

        @Test
        @DisplayName("Should create dependent with all parameters including id")
        void shouldCreateDependentWithAllParametersIncludingId() throws Exception {
            var id = 1L;
            var name = "Joao Silva";
            var cpf = new Cpf("12345678909");
            var birthDate = LocalDate.of(2010, 1, 15);

            var dependent = new Dependent(id, name, cpf, birthDate);

            assertThat(dependent.getId()).isEqualTo(id);
            assertThat(dependent.getName()).isEqualTo(name);
            assertThat(dependent.getCpf()).isEqualTo(cpf);
            assertThat(dependent.getBirthDate()).isEqualTo(birthDate);
        }

        @Test
        @DisplayName("Should create dependent without id")
        void shouldCreateDependentWithoutId() throws Exception {
            var name = "Jorge Silva";
            var cpf = new Cpf("98765432100");
            var birthDate = LocalDate.of(2015, 6, 30);

            var dependent = new Dependent(name, cpf, birthDate);

            assertThat(dependent.getId()).isNull();
            assertThat(dependent.getName()).isEqualTo(name);
            assertThat(dependent.getCpf()).isEqualTo(cpf);
            assertThat(dependent.getBirthDate()).isEqualTo(birthDate);
        }

        @Test
        @DisplayName("Should create dependent with null id")
        void shouldCreateDependentWithNullId() throws Exception {
            var name = "Test User";
            var cpf = new Cpf("11144477735");
            var birthDate = LocalDate.of(2000, 12, 25);

            var dependent = new Dependent(null, name, cpf, birthDate);

            assertThat(dependent.getId()).isNull();
            assertThat(dependent.getName()).isEqualTo(name);
        }

        @Test
        @DisplayName("Should create dependent with past birth date")
        void shouldCreateDependentWithPastBirthDate() throws Exception {
            var name = "Idoso";
            var cpf = new Cpf("12345678909");
            var birthDate = LocalDate.of(1950, 1, 1);

            var dependent = new Dependent(name, cpf, birthDate);

            assertThat(dependent.getBirthDate()).isEqualTo(birthDate);
            assertThat(dependent.getBirthDate().isBefore(LocalDate.now())).isTrue();
        }

        @Test
        @DisplayName("Should create dependent with recent birth date")
        void shouldCreateDependentWithRecentBirthDate() throws Exception {
            var name = "Baby";
            var cpf = new Cpf("12345678909");
            var birthDate = LocalDate.now().minusYears(1);

            var dependent = new Dependent(name, cpf, birthDate);

            assertThat(dependent.getBirthDate()).isEqualTo(birthDate);
        }
    }


}
