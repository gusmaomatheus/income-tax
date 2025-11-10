package br.com.matheusgusmao.incometax;

import br.com.matheusgusmao.incometax.domain.model.declaration.Declaration;
import br.com.matheusgusmao.incometax.domain.model.dependent.Dependent;
import br.com.matheusgusmao.incometax.domain.model.expense.DeductibleExpense;
import br.com.matheusgusmao.incometax.domain.model.income.Income;
import br.com.matheusgusmao.incometax.domain.service.DeclarationService;
import br.com.matheusgusmao.incometax.infra.persistence.entity.declaration.DeclarationEntity;
import br.com.matheusgusmao.incometax.infra.persistence.mapper.DeclarationMapper;
import br.com.matheusgusmao.incometax.infra.persistence.repository.DeclarationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("Mutation")
@ExtendWith(MockitoExtension.class)
class DeclarationServiceMutationTest {

    @Mock
    private DeclarationRepository declarationRepository;
    @Mock
    private DeclarationMapper declarationMapper;

    private DeclarationService declarationService;

    @Mock
    private Income mockIncome;
    @Mock
    private DeductibleExpense mockExpense;
    @Mock
    private Dependent mockDependent;

    @BeforeEach
    void setUp() {
        declarationService = new DeclarationService(declarationRepository, declarationMapper);
    }

    @Nested
    @DisplayName("Tests for findById")
    class FindByIdTests {
        @Test
        @DisplayName("Should return declaration when found")
        void shouldReturnDeclarationWhenFound() {
            var entity = new DeclarationEntity();
            var domain = new Declaration(UUID.randomUUID(), 2024);
            when(declarationRepository.findById(1L)).thenReturn(Optional.of(entity));
            when(declarationMapper.toDomain(entity)).thenReturn(domain);

            Declaration result = declarationService.findById(1L);

            assertThat(result).isNotNull();
            verify(declarationMapper).toDomain(entity);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when not found")
        void shouldThrowNotFoundWhenFindByIdIsCalled() {
            when(declarationRepository.findById(99L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> declarationService.findById(99L))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("Declaration not found with id: 99");
        }
    }

    @Nested
    @DisplayName("Tests for Unhappy Paths (EntityNotFound)")
    class UnhappyPaths {

        @BeforeEach
        void setupEmptyFind() {
            when(declarationRepository.findById(any())).thenReturn(Optional.empty());
        }

        @Test
        @DisplayName("addIncome should throw when declaration not found")
        void addIncomeShouldThrowWhenNotFound() {
            assertThatThrownBy(() -> declarationService.addIncome(99L, mockIncome))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("removeIncome should throw when declaration not found")
        void removeIncomeShouldThrowWhenNotFound() {
            assertThatThrownBy(() -> declarationService.removeIncome(99L, 1L))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("addDeductibleExpense should throw when declaration not found")
        void addDeductibleExpenseShouldThrowWhenNotFound() {
            assertThatThrownBy(() -> declarationService.addDeductibleExpense(99L, mockExpense))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("removeDeductibleExpense should throw when declaration not found")
        void removeDeductibleExpenseShouldThrowWhenNotFound() {
            assertThatThrownBy(() -> declarationService.removeDeductibleExpense(99L, 1L))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("addDependent should throw when declaration not found")
        void addDependentShouldThrowWhenNotFound() {
            assertThatThrownBy(() -> declarationService.addDependent(99L, mockDependent))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("removeDependent should throw when declaration not found")
        void removeDependentShouldThrowWhenNotFound() {
            assertThatThrownBy(() -> declarationService.removeDependent(99L, 1L))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("submitDeclaration (findAndValidateOwnership) should throw when not found")
        void submitDeclarationShouldThrowWhenNotFound() {
            assertThatThrownBy(() -> declarationService.submitDeclaration(99L, UUID.randomUUID()))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }
}