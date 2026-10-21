package com.postech.mecanica.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.postech.mecanica.infrastructure.persistence.entity.OrdemServicoItemEstoqueEntity;

public interface OrdemServicoEstoqueRepository extends JpaRepository<OrdemServicoItemEstoqueEntity, Long> {

        @Query("SELECT ose FROM OrdemServicoEstoque ose " +
                        "WHERE ose.estoque.id = :estoqueId AND ose.disponivel = false " +
                        "ORDER BY ose.ordemServico.dataHoraCriacao ASC")
        List<OrdemServicoItemEstoqueEntity> findPendentesPorEstoqueOrdenadoPorAntiguidade(
                        @Param("estoqueId") Long estoqueId);
}