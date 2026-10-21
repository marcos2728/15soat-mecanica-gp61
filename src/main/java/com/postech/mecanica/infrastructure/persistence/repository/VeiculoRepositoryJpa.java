package com.postech.mecanica.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.postech.mecanica.infrastructure.persistence.entity.VeiculoEntity;

public interface VeiculoRepositoryJpa extends JpaRepository<VeiculoEntity, Long> {

}
