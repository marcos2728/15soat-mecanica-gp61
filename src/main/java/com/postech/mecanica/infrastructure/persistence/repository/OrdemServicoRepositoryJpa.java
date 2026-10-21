package com.postech.mecanica.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.postech.mecanica.infrastructure.persistence.entity.OrdemServicoEntity;

public interface OrdemServicoRepositoryJpa extends JpaRepository<OrdemServicoEntity, Long> {

    @Query("SELECT DISTINCT os FROM OrdemServicoEntity os LEFT JOIN FETCH os.veiculo WHERE os.id = :id")
    Optional<OrdemServicoEntity> findByIdComVeiculo(@Param("id") Long id);

    @Query("SELECT DISTINCT os FROM OrdemServicoEntity os LEFT JOIN FETCH os.veiculo")
    List<OrdemServicoEntity> findAllComVeiculo();

    @Query("SELECT DISTINCT os FROM OrdemServicoEntity os LEFT JOIN FETCH os.veiculo WHERE os.veiculo.id = :veiculoId")
    List<OrdemServicoEntity> findByVeiculoId(@Param("veiculoId") Long veiculoId);

    @Query("SELECT DISTINCT os FROM OrdemServicoEntity os " +
           "LEFT JOIN FETCH os.veiculo " +
           "WHERE os.status = com.postech.mecanica.domain.enums.StatusOrdemServico.PENDENTE_ESTOQUE " +
           "AND EXISTS (SELECT 1 FROM OrdemServicoItemEstoqueEntity p WHERE p.ordemServico = os AND p.estoque.id = :estoqueId AND (p.disponivel = false OR p.disponivel IS NULL)) " +
           "ORDER BY os.dataHoraCriacao ASC")
    List<OrdemServicoEntity> findPendentesPorEstoqueId(@Param("estoqueId") Long estoqueId);

}
