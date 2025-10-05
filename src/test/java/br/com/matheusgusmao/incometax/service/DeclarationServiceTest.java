package br.com.matheusgusmao.incometax.service;

import br.com.matheusgusmao.incometax.domain.model.declaration.Declaration;
import br.com.matheusgusmao.incometax.domain.service.DeclarationService;
import br.com.matheusgusmao.incometax.infra.exception.custom.EntityAlreadyExistsException;
import br.com.matheusgusmao.incometax.infra.persistence.entity.declaration.DeclarationEntity;
import br.com.matheusgusmao.incometax.infra.persistence.mapper.DeclarationMapper;
import br.com.matheusgusmao.incometax.infra.persistence.repository.DeclarationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeclarationServiceTest {

    @Mock
    private DeclarationRepository declarationRepository;

    @Spy
    private DeclarationMapper declarationMapper = new DeclarationMapper();

    @InjectMocks
    private DeclarationService declarationService;

    @Test
    @DisplayName("[Scenario] Should create new declaration successfully")
    void shouldCreateNewDeclarationSuccessfullyWhenNonExistent() {
        final UUID taxpayerId = UUID.randomUUID();
        final int year = 2025;

        when(declarationRepository.existsByTaxpayerIdAndYear(taxpayerId, year)).thenReturn(false);
        when(declarationRepository.save(any(DeclarationEntity.class))).thenAnswer(invocation -> {
            DeclarationEntity entity = invocation.getArgument(0);
            entity.setId(1L);
            return entity;
        });

        Declaration result = declarationService.createNewDeclaration(taxpayerId, year);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(1L, result.getId());
        verify(declarationRepository).existsByTaxpayerIdAndYear(taxpayerId, year);
        verify(declarationRepository).save(any(DeclarationEntity.class));
    }

    @Test
    @DisplayName("[Scenario] Should prevent duplicate declaration in the same year")
    void shouldThrowExceptionWhenDeclarationForSameYearAlreadyExists() {
        final UUID taxpayerId = UUID.randomUUID();
        final int year = 2025;
        when(declarationRepository.existsByTaxpayerIdAndYear(taxpayerId, year)).thenReturn(true);

        assertThrows(EntityAlreadyExistsException.class,
                () -> declarationService.createNewDeclaration(taxpayerId, year)
        );

        verify(declarationRepository, never()).save(any(DeclarationEntity.class));
    }
}