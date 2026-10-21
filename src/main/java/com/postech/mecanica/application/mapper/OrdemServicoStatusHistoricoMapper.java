package com.postech.mecanica.application.mapper;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.postech.mecanica.application.dto.TempoMedioPorFaseResponseDTO;
import com.postech.mecanica.application.dto.TempoPorFaseResponseDTO;
import com.postech.mecanica.domain.enums.StatusOrdemServico;
import com.postech.mecanica.infrastructure.persistence.entity.OrdemServicoHistoricoEntity;

@Component
public class OrdemServicoStatusHistoricoMapper {

    public TempoPorFaseResponseDTO toResponseDTO(OrdemServicoHistoricoEntity historico) {
        TempoPorFaseResponseDTO dto = new TempoPorFaseResponseDTO();
        dto.setStatus(historico.getStatus());
        dto.setDataHoraInicio(historico.getDataHoraInicio());
        dto.setDataHoraFim(historico.getDataHoraFim());

        boolean emAndamento = historico.getDataHoraFim() == null;
        LocalDateTime fimOuAgora = emAndamento ? LocalDateTime.now() : historico.getDataHoraFim();

        dto.setTempoMinutos(Duration.between(historico.getDataHoraInicio(), fimOuAgora).toMinutes());
        dto.setEmAndamento(emAndamento);

        return dto;
    }

    public TempoMedioPorFaseResponseDTO toMediaResponseDTO(StatusOrdemServico status, long tempoMedioMinutos,
            int quantidadeAmostras) {
        return new TempoMedioPorFaseResponseDTO(status, tempoMedioMinutos, quantidadeAmostras);
    }
}