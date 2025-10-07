package br.com.matheusgusmao.incometax.infra.persistence.repository;

import br.com.matheusgusmao.incometax.infra.persistence.entity.declaration.DeclarationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;

@Repository
public interface DeclarationRepository extends JpaRepository<DeclarationEntity, Long> {
    boolean existsByTaxpayerIdAndYear(UUID taxpayerId, int year);

    List<DeclarationEntity> findAllByTaxpayerId(UUID taxpayerId);
}