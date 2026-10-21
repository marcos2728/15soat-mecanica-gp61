package com.postech.mecanica.application.useCase.estoque;

import org.springframework.stereotype.Service;

import com.postech.mecanica.application.dto.EstoqueResponseDTO;
import com.postech.mecanica.application.mapper.EstoqueMapper;
import com.postech.mecanica.domain.repository.EstoqueRepository;
import com.postech.mecanica.exception.ResourceNotFoundException;

@Service
public class ConsultarItemEstoqueUseCase {

    private final EstoqueRepository estoqueRepository;
    private final EstoqueMapper estoqueMapper;

    public ConsultarItemEstoqueUseCase(EstoqueRepository estoqueRepository,
            EstoqueMapper estoqueMapper) {
        this.estoqueRepository = estoqueRepository;
        this.estoqueMapper = estoqueMapper;
    }

    public EstoqueResponseDTO executar(Long id) {
        return estoqueRepository.consultarPorId(id)
                .map(estoqueMapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado com o ID: " + id));
    }
}
