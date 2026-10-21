package com.postech.mecanica.domain.repository;

import java.util.List;
import java.util.Optional;

import com.postech.mecanica.domain.entity.OrdemServico;

public interface OrdemServicoRepository {

    OrdemServico salvar(OrdemServico ordemServico);

    Optional<OrdemServico> consultarPorId(Long id);

    List<OrdemServico> buscarTodos();

    List<OrdemServico> buscarPorVeiculoId(Long veiculoId);

    List<OrdemServico> buscarPendentesPorEstoqueId(Long estoqueId);

    void deletarPorId(Long id);

    boolean existsById(Long id);

}
