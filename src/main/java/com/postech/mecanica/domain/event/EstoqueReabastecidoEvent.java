package com.postech.mecanica.domain.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EstoqueReabastecidoEvent {

    private final Long estoqueId;
    private final List<Long> ordensServicoLiberadas = new ArrayList<>();

    public EstoqueReabastecidoEvent(Long estoqueId) {
        this.estoqueId = estoqueId;
    }

    public Long getEstoqueId() {
        return estoqueId;
    }

    public void registrarOrdemLiberada(Long ordemServicoId) {
        if (ordemServicoId != null && !this.ordensServicoLiberadas.contains(ordemServicoId)) {
            this.ordensServicoLiberadas.add(ordemServicoId);
        }
    }

    public List<Long> getOrdensServicoLiberadas() {
        return Collections.unmodifiableList(ordensServicoLiberadas);
    }
}
