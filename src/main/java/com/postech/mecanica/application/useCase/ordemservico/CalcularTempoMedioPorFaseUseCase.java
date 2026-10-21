package com.postech.mecanica.application.useCase.ordemservico;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postech.mecanica.application.dto.TempoMedioPorFaseResponseDTO;
import com.postech.mecanica.application.mapper.OrdemServicoStatusHistoricoMapper;
import com.postech.mecanica.domain.enums.StatusOrdemServico;
import com.postech.mecanica.domain.repository.OrdemServicoStatusHistoricoRepository;
import com.postech.mecanica.infrastructure.persistence.entity.OrdemServicoHistoricoEntity;

@Service
public class CalcularTempoMedioPorFaseUseCase {

    private final OrdemServicoStatusHistoricoRepository ordemServicoStatusHistoricoRepository;
    private final OrdemServicoStatusHistoricoMapper ordemServicoStatusHistoricoMapper;

    public CalcularTempoMedioPorFaseUseCase(
            OrdemServicoStatusHistoricoRepository ordemServicoStatusHistoricoRepository,
            OrdemServicoStatusHistoricoMapper ordemServicoStatusHistoricoMapper) {
        this.ordemServicoStatusHistoricoRepository = ordemServicoStatusHistoricoRepository;
        this.ordemServicoStatusHistoricoMapper = ordemServicoStatusHistoricoMapper;
    }

    @Transactional(readOnly = true)
    public List<TempoMedioPorFaseResponseDTO> executar() {
        List<OrdemServicoHistoricoEntity> fechados = ordemServicoStatusHistoricoRepository.findByDataHoraFimIsNotNull();

        Map<StatusOrdemServico, List<Long>> duracoesPorStatus = new LinkedHashMap<>();
        for (OrdemServicoHistoricoEntity h : fechados) {
            long minutos = Duration.between(h.getDataHoraInicio(), h.getDataHoraFim()).toMinutes();
            duracoesPorStatus.computeIfAbsent(h.getStatus(), k -> new ArrayList<>()).add(minutos);
        }

        return duracoesPorStatus.entrySet().stream()
                .map(e -> ordemServicoStatusHistoricoMapper.toMediaResponseDTO(
                        e.getKey(),
                        (long) e.getValue().stream().mapToLong(Long::longValue).average().orElse(0),
                        e.getValue().size()))
                .toList();
    }
}
