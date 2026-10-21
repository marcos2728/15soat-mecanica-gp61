package com.postech.mecanica.domain.enums;

import java.util.Set;

public enum StatusOrdemServico {
    CRIADA,
    EM_DIAGNOSTICO,
    DIAGNOSTICO_CONCLUIDO,
    PENDENTE_ESTOQUE,
    PENDENTE_NOTIFICACAO_CLIENTE,
    PENDENTE_APROVACAO_CLIENTE,
    APROVADA,
    CANCELADA,
    EM_EXECUCAO,
    FINALIZADA,
    PENDENTE_ENTREGA,
    ENTREGUE;

    public Set<StatusOrdemServico> proximosStatusPermitidos() {
        return switch (this) {
            case CRIADA -> Set.of(EM_DIAGNOSTICO, CANCELADA);
            case EM_DIAGNOSTICO -> Set.of(DIAGNOSTICO_CONCLUIDO, PENDENTE_ESTOQUE, CANCELADA);
            case PENDENTE_ESTOQUE -> Set.of(DIAGNOSTICO_CONCLUIDO, CANCELADA);
            case DIAGNOSTICO_CONCLUIDO -> Set.of(PENDENTE_NOTIFICACAO_CLIENTE, PENDENTE_APROVACAO_CLIENTE, CANCELADA);
            case PENDENTE_NOTIFICACAO_CLIENTE -> Set.of(PENDENTE_APROVACAO_CLIENTE, APROVADA, CANCELADA);
            case PENDENTE_APROVACAO_CLIENTE -> Set.of(APROVADA, CANCELADA);
            case APROVADA -> Set.of(EM_EXECUCAO, CANCELADA);
            case EM_EXECUCAO -> Set.of(FINALIZADA, CANCELADA);
            case FINALIZADA -> Set.of(PENDENTE_ENTREGA, CANCELADA);
            case PENDENTE_ENTREGA -> Set.of(ENTREGUE, CANCELADA);
            case ENTREGUE, CANCELADA -> Set.of();
        };
    }

    public boolean podeTransitarPara(StatusOrdemServico novoStatus) {
        return proximosStatusPermitidos().contains(novoStatus);
    }
}
