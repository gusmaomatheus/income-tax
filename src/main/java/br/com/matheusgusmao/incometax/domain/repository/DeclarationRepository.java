package br.com.matheusgusmao.incometax.domain.repository;

import br.com.matheusgusmao.incometax.domain.model.declaration.Declaration;
import java.util.UUID;

public interface DeclarationRepository {
    boolean existsByTaxpayerIdAndYear(UUID taxpayerId, int year);
    Declaration save(Declaration declaration);
}