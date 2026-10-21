package com.postech.mecanica.application.useCase.ordemservico;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postech.mecanica.application.dto.OrdemServicoResponseDTO;
import com.postech.mecanica.application.mapper.OrdemServicoMapper;
import com.postech.mecanica.domain.entity.OrdemServico;
import com.postech.mecanica.domain.entity.OrdemServicoItemEstoque;
import com.postech.mecanica.domain.enums.StatusOrdemServico;
import com.postech.mecanica.domain.repository.EstoqueRepository;
import com.postech.mecanica.domain.repository.OrdemServicoRepository;
import com.postech.mecanica.exception.RegraNegocioException;
import com.postech.mecanica.exception.ResourceNotFoundException;

@Service
public class RecusarOrdemServicoUseCase {

    private static final Logger log = LoggerFactory.getLogger(RecusarOrdemServicoUseCase.class);

    private final OrdemServicoRepository ordemServicoRepository;
    private final EstoqueRepository estoqueRepository;
    private final OrdemServicoMapper ordemServicoMapper;

    public RecusarOrdemServicoUseCase(
            OrdemServicoRepository ordemServicoRepository,
            EstoqueRepository estoqueRepository,
            OrdemServicoMapper ordemServicoMapper) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.estoqueRepository = estoqueRepository;
        this.ordemServicoMapper = ordemServicoMapper;
    }

    @Transactional
    public OrdemServicoResponseDTO executar(Long id) {
        OrdemServico ordemServico = ordemServicoRepository.consultarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ordem de serviço não encontrada com ID: " + id));

        // Idempotência: caso já esteja cancelada
        if (ordemServico.getStatus() == StatusOrdemServico.CANCELADA) {
            log.info("Ordem de serviço #{} já está cancelada.", id);
            return ordemServicoMapper.toResponseDTO(ordemServico);
        }

        // Validação de regras de negócio para transição
        if (ordemServico.getStatus() != StatusOrdemServico.PENDENTE_APROVACAO_CLIENTE
                && ordemServico.getStatus() != StatusOrdemServico.PENDENTE_NOTIFICACAO_CLIENTE) {
            throw new RegraNegocioException(
                    String.format("A Ordem de Serviço #%d não pode ser recusada no status atual: %s. " +
                            "Apenas ordens aguardando aprovação podem ser recusadas.", id, ordemServico.getStatus()));
        }

        // Estorno das peças reservadas no estoque
        if (ordemServico.getPecas() != null) {
            for (OrdemServicoItemEstoque peca : ordemServico.getPecas()) {
                if (Boolean.TRUE.equals(peca.getDisponivel()) && peca.getQuantidade() != null && peca.getEstoqueId() != null) {
                    estoqueRepository.consultarPorId(peca.getEstoqueId()).ifPresent(estoque -> {
                        estoque.adicionarEstoque(peca.getQuantidade());
                        estoqueRepository.salvar(estoque);
                        log.info("Estorno de estoque realizado para item #{}: +{} unidades (OS #{})",
                                peca.getEstoqueId(), peca.getQuantidade(), id);
                    });
                }
            }
        }

        ordemServico.mudarStatus(StatusOrdemServico.CANCELADA);
        OrdemServico salva = ordemServicoRepository.salvar(ordemServico);
        log.info("Ordem de serviço #{} recusada e cancelada pelo cliente.", id);

        return ordemServicoMapper.toResponseDTO(salva);
    }
}
