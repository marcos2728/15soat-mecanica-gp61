package com.postech.mecanica.application.useCase.servico;

import org.springframework.stereotype.Service;

import com.postech.mecanica.application.dto.ServicoResponseDTO;
import com.postech.mecanica.application.mapper.ServicoMapper;
import com.postech.mecanica.domain.repository.ServicoRepository;
import com.postech.mecanica.exception.ResourceNotFoundException;

@Service
public class ConsultarServicoUseCase {

    private final ServicoRepository servicoRepository;
    private final ServicoMapper servicoMapper;

    public ConsultarServicoUseCase(ServicoRepository servicoRepository,
            ServicoMapper servicoMapper) {
        this.servicoRepository = servicoRepository;
        this.servicoMapper = servicoMapper;
    }

    public ServicoResponseDTO executar(Long id) {
        return servicoRepository.consultarPorId(id)
                .map(servicoMapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado com o ID: " + id));
    }
}
