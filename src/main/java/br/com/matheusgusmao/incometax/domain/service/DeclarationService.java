package br.com.matheusgusmao.incometax.domain.service;

import br.com.matheusgusmao.incometax.domain.model.declaration.Declaration;
import br.com.matheusgusmao.incometax.domain.repository.DeclarationRepository;
import br.com.matheusgusmao.incometax.infra.exception.custom.EntityAlreadyExistsException;

import java.util.UUID;

public class DeclarationService {

    private final DeclarationRepository declarationRepository;

    public DeclarationService(final DeclarationRepository declarationRepository) {
        this.declarationRepository = declarationRepository;
    }

    public Declaration createNewDeclaration(UUID taxpayerId, int year) {
        if (declarationRepository.existsByTaxpayerIdAndYear(taxpayerId, year)) {
            throw new EntityAlreadyExistsException("A declaration for the given taxpayer and year already exists.");
        }

        Declaration newDeclaration = new Declaration(taxpayerId, year);
        return declarationRepository.save(newDeclaration);
    }
}