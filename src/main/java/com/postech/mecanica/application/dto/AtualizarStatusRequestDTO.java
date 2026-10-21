package com.postech.mecanica.application.dto;

import com.postech.mecanica.domain.enums.StatusOrdemServico;

public class AtualizarStatusRequestDTO {

    private StatusOrdemServico status;

    public AtualizarStatusRequestDTO() {
    }

    public StatusOrdemServico getStatus() {
        return status;
    }

    public void setStatus(StatusOrdemServico status) {
        this.status = status;
    }

}