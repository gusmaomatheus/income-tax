package br.com.matheusgusmao.incometax.service;

import br.com.matheusgusmao.incometax.domain.service.DependentService;
import br.com.matheusgusmao.incometax.infra.persistence.entity.declaration.DeclarationEntity;
import br.com.matheusgusmao.incometax.infra.persistence.entity.dependent.DependentEntity;
import br.com.matheusgusmao.incometax.infra.persistence.repository.DeclarationRepository;
import br.com.matheusgusmao.incometax.infra.persistence.repository.DependentRepository;
import br.com.matheusgusmao.incometax.web.dto.dependent.CreateDependentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DependentServiceTest {

    private DependentRepository dependentRepository;
    private DeclarationRepository declarationRepository;
    private DependentService dependentService;

    @BeforeEach
    void setUp() {
        dependentRepository = mock(DependentRepository.class);
        declarationRepository = mock(DeclarationRepository.class);
        dependentService = new DependentService(dependentRepository, declarationRepository);
    }

    @Test
    @DisplayName("[Scenario] Should create valid dependent successfully")
    void shouldCreateValidDependentSuccessfully() {
        Long declarationId = 1L;
        CreateDependentRequest request = new CreateDependentRequest("Maria", "12345678901", LocalDate.of(2010, 1, 1));

        DeclarationEntity declaration = new DeclarationEntity();
        declaration.setId(declarationId);

        when(declarationRepository.findById(declarationId)).thenReturn(Optional.of(declaration));
        when(dependentRepository.findByCpf("12345678901")).thenReturn(Optional.empty());
        when(dependentRepository.save(any(DependentEntity.class))).thenAnswer(i -> {
            DependentEntity d = i.getArgument(0);
            d.setId(10L);
            return d;
        });

        var response = dependentService.addDependent(declarationId, request);

        assertNotNull(response);
        assertEquals("Maria", response.name());
        assertEquals("12345678901", response.cpf());
    }

    @Test
    @DisplayName("[Scenario] Should reject dependent with invalid CPF")
    void shouldRejectDependentWithInvalidCpf() {
        Long declarationId = 1L;
        CreateDependentRequest request = new CreateDependentRequest("João", "123", LocalDate.of(2010, 1, 1));

        DeclarationEntity declaration = new DeclarationEntity();
        declaration.setId(declarationId);

        when(declarationRepository.findById(declarationId)).thenReturn(Optional.of(declaration));

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                dependentService.addDependent(declarationId, request)
        );
        assertTrue(ex.getMessage().contains("CPF inválido"));
    }

    @Test
    @DisplayName("[Scenario] Should reject duplicate dependent")
    void shouldRejectDuplicateDependent() {
        Long declarationId = 1L;
        CreateDependentRequest request = new CreateDependentRequest("Ana", "12345678901", LocalDate.of(2010, 1, 1));

        DeclarationEntity declaration = new DeclarationEntity();
        declaration.setId(declarationId);

        when(declarationRepository.findById(declarationId)).thenReturn(Optional.of(declaration));
        when(dependentRepository.findByCpf("12345678901")).thenReturn(Optional.of(new DependentEntity()));

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                dependentService.addDependent(declarationId, request)
        );
        assertTrue(ex.getMessage().contains("já cadastrado"));
    }
}