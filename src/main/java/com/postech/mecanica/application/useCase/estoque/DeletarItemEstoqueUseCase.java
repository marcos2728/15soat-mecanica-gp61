package com.postech.mecanica.application.useCase.estoque;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postech.mecanica.domain.repository.EstoqueRepository;
import com.postech.mecanica.domain.repository.OrdemServicoRepository;
import com.postech.mecanica.exception.RegraNegocioException;
import com.postech.mecanica.exception.ResourceNotFoundException;

@Service
public class DeletarItemEstoqueUseCase {

    private final EstoqueRepository estoqueRepository;
    private final OrdemServicoRepository ordemServicoRepository;

    public DeletarItemEstoqueUseCase(EstoqueRepository estoqueRepository,
            OrdemServicoRepository ordemServicoRepository) {
        this.estoqueRepository = estoqueRepository;
        this.ordemServicoRepository = ordemServicoRepository;
    }

    @Transactional
    public void executar(Long idEstoque) {
        if (!estoqueRepository.existsById(idEstoque)) {
            throw new ResourceNotFoundException("Item não encontrado com o ID: " + idEstoque);
        }
        if (!ordemServicoRepository.buscarPendentesPorEstoqueId(idEstoque).isEmpty()) {
            throw new RegraNegocioException(
                    "Não é possível excluir o item pois existem Ordens de Serviço aguardando sua reposição.");
        }

        estoqueRepository.deletarPorId(idEstoque);
    }
}
