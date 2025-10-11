package br.com.matheusgusmao.incometax.service;

import br.com.matheusgusmao.incometax.domain.service.DependentService;
import br.com.matheusgusmao.incometax.infra.persistence.entity.declaration.DeclarationEntity;
import br.com.matheusgusmao.incometax.infra.persistence.entity.dependent.DependentEntity;
import br.com.matheusgusmao.incometax.infra.persistence.mapper.DependentMapper;
import br.com.matheusgusmao.incometax.infra.persistence.repository.DeclarationRepository;
import br.com.matheusgusmao.incometax.infra.persistence.repository.DependentRepository;
import br.com.matheusgusmao.incometax.web.dto.dependent.CreateDependentRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DependentService")
class DependentServiceTest {

    @Mock
    private DependentRepository dependentRepository;
    @Mock
    private DeclarationRepository declarationRepository;
    @Mock
    private DependentMapper dependentMapper;
    @InjectMocks
    private DependentService dependentService;

    @Nested
    @DisplayName("Given a taxpayer wants to add a dependent to their declaration")
    class AddDependentTests {

        @Test
        @DisplayName("When taxpayer adds valid dependent Then dependent should be created successfully")
        void shouldCreateValidDependentSuccessfully() {
            // Given
            var declarationId = 1L;
            var request = new CreateDependentRequest("Maria", "12345678901", LocalDate.of(2010, 1, 1));

            var declaration = new DeclarationEntity();
            declaration.setId(declarationId);

            when(declarationRepository.findById(declarationId)).thenReturn(Optional.of(declaration));
            when(dependentRepository.findByCpf("12345678901")).thenReturn(Optional.empty());
            when(dependentRepository.save(any(DependentEntity.class))).thenAnswer(i -> {
                var d = i.getArgument(0);
                d.setId(10L);
                return d;
            });

            // When
            var response = dependentService.addDependent(declarationId, request);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.name()).isEqualTo("Maria");
            assertThat(response.cpf()).isEqualTo("12345678901");
        }

        @Test
        @DisplayName("When taxpayer tries to add dependent with invalid CPF Then exception should be thrown")
        void shouldRejectDependentWithInvalidCpf() {
            // Given
            var declarationId = 1L;
            var request = new CreateDependentRequest("João", "123", LocalDate.of(2010, 1, 1));

            var declaration = new DeclarationEntity();
            declaration.setId(declarationId);

            when(declarationRepository.findById(declarationId)).thenReturn(Optional.of(declaration));

            // When & Then
            assertThatThrownBy(() -> dependentService.addDependent(declarationId, request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("CPF inválido");
        }

        @Test
        @DisplayName("When taxpayer tries to add duplicate dependent Then exception should be thrown")
        void shouldRejectDuplicateDependent() {
            // Given
            var declarationId = 1L;
            var request = new CreateDependentRequest("Ana", "12345678901", LocalDate.of(2010, 1, 1));

            var declaration = new DeclarationEntity();
            declaration.setId(declarationId);

            when(declarationRepository.findById(declarationId)).thenReturn(Optional.of(declaration));
            when(dependentRepository.findByCpf("12345678901")).thenReturn(Optional.of(new DependentEntity()));

            // When & Then
            assertThatThrownBy(() -> dependentService.addDependent(declarationId, request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("já cadastrado");
        }
    }
}