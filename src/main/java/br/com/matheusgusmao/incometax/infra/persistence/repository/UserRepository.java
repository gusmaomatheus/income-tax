package br.com.matheusgusmao.incometax.infra.persistence.repository;

import br.com.matheusgusmao.incometax.infra.persistence.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(String email);
}