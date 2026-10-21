package com.postech.mecanica.application.useCase.ordemservico;

import org.springframework.stereotype.Service;

import com.postech.mecanica.application.dto.OrdemServicoResponseDTO;
import com.postech.mecanica.application.mapper.OrdemServicoMapper;
import com.postech.mecanica.domain.entity.OrdemServico;
import com.postech.mecanica.domain.repository.OrdemServicoRepository;
import com.postech.mecanica.exception.ResourceNotFoundException;

@Service
public class ConsultarOrdemServicoUseCase {

    private final OrdemServicoRepository ordemServicoRepository;
    private final OrdemServicoMapper ordemServicoMapper;

    public ConsultarOrdemServicoUseCase(OrdemServicoRepository ordemServicoRepository,
            OrdemServicoMapper ordemServicoMapper) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.ordemServicoMapper = ordemServicoMapper;
    }

    public OrdemServicoResponseDTO executar(Long id) {
        OrdemServico ordemServico = ordemServicoRepository.consultarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ordem de serviço não encontrada"));
        return ordemServicoMapper.toResponseDTO(ordemServico);
    }
}
