package com.postech.mecanica.application.useCase.ordemservico;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postech.mecanica.domain.entity.Estoque;
import com.postech.mecanica.domain.entity.OrdemServico;
import com.postech.mecanica.domain.entity.OrdemServicoItemEstoque;
import com.postech.mecanica.domain.enums.StatusOrdemServico;
import com.postech.mecanica.domain.repository.EstoqueRepository;
import com.postech.mecanica.domain.repository.OrdemServicoRepository;
import com.postech.mecanica.exception.ResourceNotFoundException;

@Service
public class ReconciliarPecasPendentesUseCase {

    private final OrdemServicoRepository ordemServicoRepository;
    private final EstoqueRepository estoqueRepository;

    public ReconciliarPecasPendentesUseCase(
            OrdemServicoRepository ordemServicoRepository,
            EstoqueRepository estoqueRepository) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.estoqueRepository = estoqueRepository;
    }

    @Transactional
    public void executar(Long idItemEstoque) {
        Estoque estoque = estoqueRepository.consultarPorId(idItemEstoque)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Item de estoque não encontrado com o ID: " + idItemEstoque));

        List<OrdemServico> ordensPendentes = ordemServicoRepository.buscarPendentesPorEstoqueId(estoque.getId());

        for (OrdemServico ordemServico : ordensPendentes) {
            if (ordemServico.getStatus() != StatusOrdemServico.PENDENTE_ESTOQUE) {
                continue;
            }

            for (OrdemServicoItemEstoque item : ordemServico.getPecas()) {
                if (estoque.getId().equals(item.getEstoqueId()) && !Boolean.TRUE.equals(item.getDisponivel())) {
                    if (estoque.temEstoqueSuficiente(item.getQuantidade())) {
                        item.marcarComoDisponivel(estoque.getValorItem());
                        estoque.baixarEstoque(item.getQuantidade());
                    }
                }
            }

            ordemServico.calcularValorTotal();

            if (ordemServico.todasPecasDisponiveis()) {
                ordemServico.transitarPara(StatusOrdemServico.DIAGNOSTICO_CONCLUIDO);
            }

            ordemServicoRepository.salvar(ordemServico);
        }

        estoqueRepository.salvar(estoque);
    }
}
