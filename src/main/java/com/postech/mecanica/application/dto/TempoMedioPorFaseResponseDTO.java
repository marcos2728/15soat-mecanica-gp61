package com.postech.mecanica.application.dto;

import com.postech.mecanica.domain.enums.StatusOrdemServico;

public class TempoMedioPorFaseResponseDTO {

    private StatusOrdemServico status;
    private Long tempoMedioMinutos;
    private Integer quantidadeAmostras;

    public TempoMedioPorFaseResponseDTO() {
    }

    public TempoMedioPorFaseResponseDTO(StatusOrdemServico status, Long tempoMedioMinutos, Integer quantidadeAmostras) {
        this.status = status;
        this.tempoMedioMinutos = tempoMedioMinutos;
        this.quantidadeAmostras = quantidadeAmostras;
    }

    public StatusOrdemServico getStatus() {
        return status;
    }

    public void setStatus(StatusOrdemServico status) {
        this.status = status;
    }

    public Long getTempoMedioMinutos() {
        return tempoMedioMinutos;
    }

    public void setTempoMedioMinutos(Long tempoMedioMinutos) {
        this.tempoMedioMinutos = tempoMedioMinutos;
    }

    public Integer getQuantidadeAmostras() {
        return quantidadeAmostras;
    }

    public void setQuantidadeAmostras(Integer quantidadeAmostras) {
        this.quantidadeAmostras = quantidadeAmostras;
    }

}