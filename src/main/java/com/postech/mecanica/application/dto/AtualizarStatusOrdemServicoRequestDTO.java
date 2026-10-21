package com.postech.mecanica.application.dto;

import com.postech.mecanica.domain.enums.StatusOrdemServico;

public class AtualizarStatusOrdemServicoRequestDTO {

    private StatusOrdemServico status;

    public AtualizarStatusOrdemServicoRequestDTO() {
    }

    public StatusOrdemServico getStatus() {
        return status;
    }

    public void setStatus(StatusOrdemServico status) {
        this.status = status;
    }

}