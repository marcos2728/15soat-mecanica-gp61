package com.postech.mecanica.application.useCase.veiculo;

import org.springframework.stereotype.Service;

import com.postech.mecanica.application.dto.VeiculoResponseDTO;
import com.postech.mecanica.application.mapper.VeiculoMapper;
import com.postech.mecanica.domain.entity.Veiculo;
import com.postech.mecanica.domain.repository.VeiculoRepository;
import com.postech.mecanica.exception.ResourceNotFoundException;

@Service
public class ConsultarVeiculoUseCase {

    private final VeiculoRepository veiculoRepository;
    private final VeiculoMapper veiculoMapper;

    public ConsultarVeiculoUseCase(VeiculoRepository veiculoRepository, VeiculoMapper veiculoMapper) {
        this.veiculoRepository = veiculoRepository;
        this.veiculoMapper = veiculoMapper;
    }

    public VeiculoResponseDTO executar(Long idVeiculo) {
        Veiculo veiculo = veiculoRepository.consultarPorId(idVeiculo)
                .orElseThrow(() -> new ResourceNotFoundException("Veiculo não encontrado com o ID: " + idVeiculo));

        return veiculoMapper.toDTO(veiculo);
    }
}
