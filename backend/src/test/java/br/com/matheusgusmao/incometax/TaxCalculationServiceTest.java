package br.com.matheusgusmao.incometax;

import br.com.matheusgusmao.incometax.domain.model.declaration.Declaration;
import br.com.matheusgusmao.incometax.domain.model.declaration.TaxCalculationResult;
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

@Tag("UnitTest")
@Tag("Unit")
@Tag("Mutation")
@ExtendWith(MockitoExtension.class)
public class TaxCalculationServiceTest {
    @Mock
    private DeclarationRepository declarationRepository;

    @Mock
    private DeclarationMapper declarationMapper;

    private TaxCalculationService taxCalculationService;

    private final DeclarationEntity mockEntity = new DeclarationEntity();
    private final Long declarationId = 1L;

    @BeforeEach
    void setUp() {
        taxCalculationService = new TaxCalculationService(declarationRepository, declarationMapper);
    }

    private Declaration setupMockDeclaration(String calculationBase) {
        Declaration mockDeclaration = mock(Declaration.class);
        when(declarationRepository.findById(declarationId)).thenReturn(Optional.of(mockEntity));
        when(declarationMapper.toDomain(mockEntity)).thenReturn(mockDeclaration);

        BigDecimal base = new BigDecimal(calculationBase);
        when(mockDeclaration.calculateTotalIncome()).thenReturn(base);
        when(mockDeclaration.calculateTotalDeductions()).thenReturn(BigDecimal.ZERO);

        return mockDeclaration;
    }

    @Test
    @Tag("Mutation")
    @DisplayName("Should verify mapper call (Kills Line 33 mutant)")
    void shouldVerifyMapperCall() {
        setupMockDeclaration("0");
        taxCalculationService.calculate(declarationId);
        verify(declarationMapper).toDomain(mockEntity);
    }


    @Test
    @Tag("Mutation")
    @DisplayName("Should check scale and aliquot (Kills Line 51 and 69 mutants)")
    void shouldCheckScaleAndAliquot() {
        setupMockDeclaration("40000.00");
        TaxCalculationResult result = taxCalculationService.calculate(declarationId);

        assertThat(result.taxDue()).isEqualTo(new BigDecimal("1617.62"));
        assertThat(result.taxDue().scale()).isEqualTo(2);
        assertThat(result.effectiveAliquot()).isEqualTo(new BigDecimal("4.0400"));
    }

    @Nested
    @Tag("Mutation")
    @DisplayName("Boundary Condition Tests")
    class BoundaryTests {

        @Test
        @DisplayName("Should calculate correctly at FAIXA 1 LIMIT (Kills Line 60 mutants)")
        void testAtFaixa1Boundary() {
            setupMockDeclaration("24511.92");
            TaxCalculationResult result = taxCalculationService.calculate(declarationId);
            assertThat(result.taxDue()).isEqualByComparingTo("0.00");
            assertThat(result.taxDue().scale()).as("Scale must be 2").isEqualTo(2);
        }

        @Test
        @DisplayName("Should calculate correctly at FAIXA 2 LIMIT")
        void testAtFaixa2Boundary() {
            setupMockDeclaration("33919.80");
            TaxCalculationResult result = taxCalculationService.calculate(declarationId);
            assertThat(result.taxDue()).isEqualByComparingTo("705.60");
            assertThat(result.taxDue().scale()).isEqualTo(2);
        }

        @Test
        @DisplayName("Should calculate correctly at FAIXA 3 LIMIT")
        void testAtFaixa3Boundary() {
            setupMockDeclaration("45012.60");
            TaxCalculationResult result = taxCalculationService.calculate(declarationId);
            assertThat(result.taxDue()).isEqualByComparingTo("2369.51");
            assertThat(result.taxDue().scale()).isEqualTo(2);
        }

        @Test
        @DisplayName("Should calculate correctly at FAIXA 4 LIMIT")
        void testAtFaixa4Boundary() {
            setupMockDeclaration("55976.16");
            TaxCalculationResult result = taxCalculationService.calculate(declarationId);
            assertThat(result.taxDue()).isEqualByComparingTo("4641.40");
            assertThat(result.taxDue().scale()).isEqualTo(2);
        }
    }
}
