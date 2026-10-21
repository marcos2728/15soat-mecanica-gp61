package com.postech.mecanica.application.useCase.ordemservico;

import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.postech.mecanica.domain.entity.Estoque;
import com.postech.mecanica.domain.entity.OrdemServico;
import com.postech.mecanica.domain.entity.OrdemServicoItemEstoque;
import com.postech.mecanica.domain.enums.StatusOrdemServico;
import com.postech.mecanica.domain.event.EstoqueReabastecidoEvent;
import com.postech.mecanica.domain.repository.EstoqueRepository;
import com.postech.mecanica.domain.repository.OrdemServicoRepository;

@Component
public class ReconciliacaoEstoqueListener {

    private final OrdemServicoRepository ordemServicoRepository;
    private final EstoqueRepository estoqueRepository;

    public ReconciliacaoEstoqueListener(
            OrdemServicoRepository ordemServicoRepository,
            EstoqueRepository estoqueRepository) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.estoqueRepository = estoqueRepository;
    }

    @EventListener
    public void aoReabastecerEstoque(EstoqueReabastecidoEvent evento) {
        if (evento == null || evento.getEstoqueId() == null) {
            return;
        }

        Estoque estoque = estoqueRepository.consultarPorId(evento.getEstoqueId()).orElse(null);
        if (estoque == null) {
            return;
        }

        List<OrdemServico> ordensPendentes = ordemServicoRepository.buscarPendentesPorEstoqueId(estoque.getId());

        for (OrdemServico ordemServico : ordensPendentes) {
            if (ordemServico.getStatus() != StatusOrdemServico.PENDENTE_ESTOQUE) {
                continue;
            }

            boolean ordemAtualizada = false;

            for (OrdemServicoItemEstoque item : ordemServico.getPecas()) {
                if (estoque.getId().equals(item.getEstoqueId()) && !Boolean.TRUE.equals(item.getDisponivel())) {
                    if (estoque.temEstoqueSuficiente(item.getQuantidade())) {
                        item.marcarComoDisponivel(estoque.getValorItem());
                        estoque.baixarEstoque(item.getQuantidade());
                        ordemAtualizada = true;
                    }
                }
            }

            if (ordemAtualizada) {
                ordemServico.calcularValorTotal();

                if (ordemServico.todasPecasDisponiveis()) {
                    ordemServico.transitarPara(StatusOrdemServico.DIAGNOSTICO_CONCLUIDO);
                    evento.registrarOrdemLiberada(ordemServico.getId());
                }

                ordemServicoRepository.salvar(ordemServico);
            }
        }

        estoqueRepository.salvar(estoque);
    }
}
