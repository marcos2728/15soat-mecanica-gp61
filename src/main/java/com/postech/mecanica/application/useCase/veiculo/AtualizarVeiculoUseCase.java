package com.postech.mecanica.application.useCase.veiculo;

import org.springframework.stereotype.Service;

import com.postech.mecanica.application.dto.VeiculoRequestDTO;
import com.postech.mecanica.application.dto.VeiculoResponseDTO;
import com.postech.mecanica.application.mapper.VeiculoMapper;
import com.postech.mecanica.domain.entity.Veiculo;
import com.postech.mecanica.domain.repository.VeiculoRepository;
import com.postech.mecanica.exception.ResourceNotFoundException;

@Service
public class AtualizarVeiculoUseCase {

    private final VeiculoRepository veiculoRepository;
    private final VeiculoMapper veiculoMapper;

    public AtualizarVeiculoUseCase(VeiculoRepository veiculoRepository, VeiculoMapper veiculoMapper) {
        this.veiculoRepository = veiculoRepository;
        this.veiculoMapper = veiculoMapper;
    }

    public VeiculoResponseDTO executar(Long idVeiculo, VeiculoRequestDTO veiculoDTO) {
        Veiculo veiculoExistente = veiculoRepository.consultarPorId(idVeiculo)
                .orElseThrow(() -> new ResourceNotFoundException("Veiculo não encontrado com o ID: " + idVeiculo));

        // Atualiza apenas os campos que podem ser editados

        veiculoExistente.atualizarDados(
                veiculoDTO.marca(),
                veiculoDTO.modelo(),
                veiculoDTO.anoFabricacao());
        Veiculo veiculoSalvo = veiculoRepository.salvar(veiculoExistente);
        return veiculoMapper.toDTO(veiculoSalvo);
    }
}
