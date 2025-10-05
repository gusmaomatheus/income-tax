package br.com.matheusgusmao.incometax.service;

import br.com.matheusgusmao.incometax.domain.model.declaration.Declaration;
import br.com.matheusgusmao.incometax.domain.model.declaration.DeclarationStatus;
import br.com.matheusgusmao.incometax.domain.repository.DeclarationRepository;
import br.com.matheusgusmao.incometax.domain.service.DeclarationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DeclarationServiceTest {

    @Mock
    private DeclarationRepository declarationRepository;

    @InjectMocks
    private DeclarationService declarationService;

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("[Scenario] Should create new declaration successfully")
    void shouldCreateNewDeclarationSuccessfullyWhenNonExistent() {
        final UUID taxpayerId = UUID.randomUUID();
        final int year = 2025;
        final Declaration savedDeclaration = new Declaration(1L, taxpayerId, year, DeclarationStatus.EDITING);

        when(declarationRepository.existsByTaxpayerIdAndYear(taxpayerId, year)).thenReturn(false);
        when(declarationRepository.save(any(Declaration.class))).thenReturn(savedDeclaration);

        Declaration result = declarationService.createNewDeclaration(taxpayerId, year);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(1L, result.getId());
        assertEquals(taxpayerId, result.getTaxpayerId());
        assertEquals(year, result.getYear());
        assertEquals(DeclarationStatus.EDITING, result.getStatus());

        verify(declarationRepository).existsByTaxpayerIdAndYear(taxpayerId, year);
        verify(declarationRepository).save(any(Declaration.class));
    }
}