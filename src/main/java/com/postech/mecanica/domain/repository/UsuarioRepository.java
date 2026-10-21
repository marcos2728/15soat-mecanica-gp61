package com.postech.mecanica.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.postech.mecanica.infrastructure.persistence.entity.UsuarioEntity;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
    Optional<UsuarioEntity> findByLogin(String login);
}