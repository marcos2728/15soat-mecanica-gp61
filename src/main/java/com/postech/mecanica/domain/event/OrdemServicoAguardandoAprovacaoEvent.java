package com.postech.mecanica.domain.event;

/**
 * Evento de domínio disparado quando uma Ordem de Serviço transita
 * para o status de aguardando aprovação do cliente.
 */
public class OrdemServicoAguardandoAprovacaoEvent {

    private final Long ordemServicoId;

    public OrdemServicoAguardandoAprovacaoEvent(Long ordemServicoId) {
        this.ordemServicoId = ordemServicoId;
    }

    public Long getOrdemServicoId() {
        return ordemServicoId;
    }
}
