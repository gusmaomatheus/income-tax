package br.com.matheusgusmao.incometax.infra.persistence.repository;

import br.com.matheusgusmao.incometax.infra.persistence.entity.dependent.DependentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DependentRepository extends JpaRepository<DependentEntity, Long> {
    Optional<DependentEntity> findByCpf(String cpf);
}