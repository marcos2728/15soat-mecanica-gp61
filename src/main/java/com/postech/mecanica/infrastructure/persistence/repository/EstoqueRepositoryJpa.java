package com.postech.mecanica.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.postech.mecanica.infrastructure.persistence.entity.EstoqueEntity;

public interface EstoqueRepositoryJpa extends JpaRepository<EstoqueEntity, Long> {

}
