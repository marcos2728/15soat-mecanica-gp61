package com.postech.mecanica.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.postech.mecanica.infrastructure.persistence.entity.OrdemServicoHistoricoEntity;

public interface OrdemServicoStatusHistoricoRepository extends JpaRepository<OrdemServicoHistoricoEntity, Long> {

    @Query("SELECT o FROM OrdemServicoStatusHistorico o WHERE o.ordemServico.id = :ordemServicoId AND o.dataHoraFim IS NULL")
    Optional<OrdemServicoHistoricoEntity> findByOrdemServicoIdAndDataHoraFimIsNull(Long ordemServicoId);

    @Query("SELECT o FROM OrdemServicoStatusHistorico o WHERE o.ordemServico.id = :ordemServicoId ORDER BY o.dataHoraInicio asc")
    List<OrdemServicoHistoricoEntity> consultarPorOrdemServicoId(Long ordemServicoId);

    default List<OrdemServicoHistoricoEntity> findByOrdemServicoIdOrderByDataHoraInicioAsc(Long ordemServicoId) {
        return consultarPorOrdemServicoId(ordemServicoId);
    }

    @Query("SELECT o FROM OrdemServicoStatusHistorico o WHERE o.dataHoraFim IS NOT NULL")
    List<OrdemServicoHistoricoEntity> findByDataHoraFimIsNotNull();
}