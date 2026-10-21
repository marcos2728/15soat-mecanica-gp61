package com.postech.mecanica.application.useCase.estoque;

import org.springframework.stereotype.Service;

import com.postech.mecanica.application.dto.EstoqueRequestDTO;
import com.postech.mecanica.application.dto.EstoqueResponseDTO;
import com.postech.mecanica.application.mapper.EstoqueMapper;
import com.postech.mecanica.domain.entity.Estoque;
import com.postech.mecanica.domain.repository.EstoqueRepository;

@Service
public class CadastrarItemEstoqueUseCase {

    private final EstoqueRepository estoqueRepository;
    private final EstoqueMapper estoqueMapper;

    public CadastrarItemEstoqueUseCase(EstoqueRepository estoqueRepository, EstoqueMapper estoqueMapper) {
        this.estoqueRepository = estoqueRepository;
        this.estoqueMapper = estoqueMapper;
    }

    public EstoqueResponseDTO executar(EstoqueRequestDTO estoqueDTO) {
        Estoque estoque = estoqueMapper.toDomain(estoqueDTO);
        return estoqueMapper.toResponseDTO(estoqueRepository.salvar(estoque));
    }
}