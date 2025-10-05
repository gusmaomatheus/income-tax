package br.com.matheusgusmao.incometax.infra.persistence.repository;

import br.com.matheusgusmao.incometax.infra.persistence.entity.declaration.DeclarationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DeclarationRepository extends JpaRepository<DeclarationEntity, Long> {
    boolean existsByTaxpayerIdAndYear(UUID taxpayerId, int year);
}