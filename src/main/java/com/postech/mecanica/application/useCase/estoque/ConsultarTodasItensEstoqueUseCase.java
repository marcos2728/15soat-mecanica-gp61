package com.postech.mecanica.application.useCase.estoque;

import java.util.List;

import org.springframework.stereotype.Service;

import com.postech.mecanica.application.dto.EstoqueResponseDTO;
import com.postech.mecanica.application.mapper.EstoqueMapper;
import com.postech.mecanica.domain.repository.EstoqueRepository;

@Service
public class ConsultarTodasItensEstoqueUseCase {

    private final EstoqueRepository estoqueRepository;
    private final EstoqueMapper estoqueMapper;

    public ConsultarTodasItensEstoqueUseCase(EstoqueRepository estoqueRepository,
            EstoqueMapper estoqueMapper) {
        this.estoqueRepository = estoqueRepository;
        this.estoqueMapper = estoqueMapper;
    }

    public List<EstoqueResponseDTO> executar() {
        return estoqueRepository.consultarTodos().stream()
                .map(estoqueMapper::toResponseDTO)
                .toList();
    }
}
