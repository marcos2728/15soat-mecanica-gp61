package com.postech.mecanica.application.useCase.ordemservico;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postech.mecanica.application.dto.TempoPorFaseResponseDTO;
import com.postech.mecanica.application.mapper.OrdemServicoStatusHistoricoMapper;
import com.postech.mecanica.domain.repository.OrdemServicoRepository;
import com.postech.mecanica.domain.repository.OrdemServicoStatusHistoricoRepository;
import com.postech.mecanica.exception.ResourceNotFoundException;

@Service
public class BuscarTemposPorOrdemServicoUseCase {

    private final OrdemServicoRepository ordemServicoRepository;
    private final OrdemServicoStatusHistoricoRepository ordemServicoStatusHistoricoRepository;
    private final OrdemServicoStatusHistoricoMapper ordemServicoStatusHistoricoMapper;

    public BuscarTemposPorOrdemServicoUseCase(
            OrdemServicoRepository ordemServicoRepository,
            OrdemServicoStatusHistoricoRepository ordemServicoStatusHistoricoRepository,
            OrdemServicoStatusHistoricoMapper ordemServicoStatusHistoricoMapper) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.ordemServicoStatusHistoricoRepository = ordemServicoStatusHistoricoRepository;
        this.ordemServicoStatusHistoricoMapper = ordemServicoStatusHistoricoMapper;
    }

    @Transactional(readOnly = true)
    public List<TempoPorFaseResponseDTO> executar(Long id) {
        ordemServicoRepository.consultarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ordem de serviço não encontrada"));

        return ordemServicoStatusHistoricoRepository.consultarPorOrdemServicoId(id).stream()
                .map(ordemServicoStatusHistoricoMapper::toResponseDTO)
                .toList();
    }
}
