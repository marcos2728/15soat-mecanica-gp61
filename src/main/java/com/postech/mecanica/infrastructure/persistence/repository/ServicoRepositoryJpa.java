package com.postech.mecanica.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.postech.mecanica.infrastructure.persistence.entity.ServicoEntity;

public interface ServicoRepositoryJpa extends JpaRepository<ServicoEntity, Long> {

}
