package com.postech.mecanica.application.useCase.ordemservico;

import org.springframework.stereotype.Service;

import com.postech.mecanica.application.dto.OrdemServicoResponseDTO;
import com.postech.mecanica.application.mapper.OrdemServicoMapper;
import com.postech.mecanica.domain.entity.OrdemServico;
import com.postech.mecanica.domain.enums.StatusOrdemServico;
import com.postech.mecanica.domain.repository.OrdemServicoRepository;
import com.postech.mecanica.exception.RegraNegocioException;

@Service
public class AtualizarStatusOrdemUseCase {

    private final OrdemServicoRepository ordemServicoRepository;
    private final OrdemServicoMapper ordemServicoMapper;

    public AtualizarStatusOrdemUseCase(OrdemServicoRepository ordemServicoRepository,
            OrdemServicoMapper ordemServicoMapper) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.ordemServicoMapper = ordemServicoMapper;
    }

    public OrdemServicoResponseDTO executar(Long id, StatusOrdemServico novoStatus) {
        if (novoStatus == StatusOrdemServico.DIAGNOSTICO_CONCLUIDO) {
            throw new RegraNegocioException("Para concluir o diagnóstico, utilize o fluxo específico de conclusão");
        }
        OrdemServico ordemServico = ordemServicoRepository.consultarPorId(id)
                .orElseThrow(() -> new RegraNegocioException("Ordem de serviço não encontrada"));

        ordemServico.mudarStatus(novoStatus);

        return ordemServicoMapper.toResponseDTO(ordemServicoRepository.salvar(ordemServico));
    }

}
