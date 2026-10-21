package com.postech.mecanica.application.useCase.servico;

import java.util.List;

import org.springframework.stereotype.Service;

import com.postech.mecanica.application.dto.ServicoResponseDTO;
import com.postech.mecanica.application.mapper.ServicoMapper;
import com.postech.mecanica.domain.repository.ServicoRepository;

@Service
public class ConsultarTodosServicosUseCase {

    private final ServicoRepository servicoRepository;
    private final ServicoMapper servicoMapper;

    public ConsultarTodosServicosUseCase(ServicoRepository servicoRepository, ServicoMapper servicoMapper) {
        this.servicoRepository = servicoRepository;
        this.servicoMapper = servicoMapper;
    }

    public List<ServicoResponseDTO> executar() {
        return servicoRepository.consultarTodos().stream()
                .map(servicoMapper::toResponseDTO)
                .toList();
    }
}
