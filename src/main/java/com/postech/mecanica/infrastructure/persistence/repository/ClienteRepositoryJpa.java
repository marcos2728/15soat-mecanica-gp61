package com.postech.mecanica.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.postech.mecanica.infrastructure.persistence.entity.ClienteEntity;

public interface ClienteRepositoryJpa extends JpaRepository<ClienteEntity, Long> {

    @Query("SELECT DISTINCT c FROM ClienteEntity c LEFT JOIN FETCH c.veiculos WHERE c.cpfCnpj = :cpfCnpj")
    Optional<ClienteEntity> findByCpfCnpjComVeiculo(@Param("cpfCnpj") String cpfCnpj);

    @Query("SELECT DISTINCT c FROM ClienteEntity c LEFT JOIN FETCH c.veiculos WHERE c.id = :id")
    Optional<ClienteEntity> findByIdComVeiculo(@Param("id") Long id);

    @Query("SELECT DISTINCT c FROM ClienteEntity c LEFT JOIN FETCH c.veiculos")
    List<ClienteEntity> findAllComVeiculo();

    @Query("SELECT DISTINCT c FROM ClienteEntity c LEFT JOIN FETCH c.veiculos v WHERE v.id = :veiculoId")
    Optional<ClienteEntity> findByVeiculoId(@Param("veiculoId") Long veiculoId);
}
