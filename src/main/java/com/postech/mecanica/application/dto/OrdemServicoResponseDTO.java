package com.postech.mecanica.application.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.postech.mecanica.domain.enums.StatusOrdemServico;

public record OrdemServicoResponseDTO(
        Long id,
        Long veiculoId,
        String placaVeiculo,
        StatusOrdemServico status,
        String descricaoProblema,
        List<ItemEstoqueResponseDTO> pecas,
        List<ItemServicoResponseDTO> servicos,
        Double valorTotal,
        LocalDateTime dataHoraCriacao,
        LocalDateTime dataHoraAtualizacao) {

    public OrdemServicoResponseDTO(
            Long id,
            Long veiculoId,
            String placaVeiculo,
            StatusOrdemServico status,
            List<ItemEstoqueResponseDTO> pecas,
            List<ItemServicoResponseDTO> servicos,
            Double valorTotal,
            LocalDateTime dataHoraCriacao,
            LocalDateTime dataHoraAtualizacao) {
        this(id, veiculoId, placaVeiculo, status, null, pecas, servicos, valorTotal, dataHoraCriacao, dataHoraAtualizacao);
    }
}