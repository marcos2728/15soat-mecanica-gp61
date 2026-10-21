package com.postech.mecanica.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.postech.mecanica.infrastructure.persistence.entity.ServicoEntity;

public interface ServicoRepository extends JpaRepository<ServicoEntity, Long> {

}
