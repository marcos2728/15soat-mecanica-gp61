package com.postech.mecanica.application.dto;

import java.time.LocalDateTime;

import com.postech.mecanica.domain.enums.StatusOrdemServico;

public class TempoPorFaseResponseDTO {

    private StatusOrdemServico status;
    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;
    private Long tempoMinutos;
    private Boolean emAndamento;

    public TempoPorFaseResponseDTO() {
    }

    public StatusOrdemServico getStatus() {
        return status;
    }

    public void setStatus(StatusOrdemServico status) {
        this.status = status;
    }

    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    public void setDataHoraInicio(LocalDateTime dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
    }

    public LocalDateTime getDataHoraFim() {
        return dataHoraFim;
    }

    public void setDataHoraFim(LocalDateTime dataHoraFim) {
        this.dataHoraFim = dataHoraFim;
    }

    public Long getTempoMinutos() {
        return tempoMinutos;
    }

    public void setTempoMinutos(Long tempoMinutos) {
        this.tempoMinutos = tempoMinutos;
    }

    public Boolean getEmAndamento() {
        return emAndamento;
    }

    public void setEmAndamento(Boolean emAndamento) {
        this.emAndamento = emAndamento;
    }

}