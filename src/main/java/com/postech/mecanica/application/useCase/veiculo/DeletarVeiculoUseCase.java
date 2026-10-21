package com.postech.mecanica.application.useCase.veiculo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postech.mecanica.domain.repository.VeiculoRepository;
import com.postech.mecanica.exception.ResourceNotFoundException;

@Service
public class DeletarVeiculoUseCase {

    private final VeiculoRepository veiculoRepository;

    public DeletarVeiculoUseCase(VeiculoRepository veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }

    @Transactional
    public void executar(Long id) {
        if (!veiculoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Veículo não encontrado com o ID: " + id);
        }
        veiculoRepository.deletarPorId(id);
    }
}
