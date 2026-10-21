package com.postech.mecanica.application.useCase.ordemservico;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postech.mecanica.application.dto.OrdemServicoRequestDTO;
import com.postech.mecanica.application.dto.OrdemServicoResponseDTO;
import com.postech.mecanica.application.mapper.OrdemServicoMapper;
import com.postech.mecanica.domain.entity.OrdemServico;
import com.postech.mecanica.domain.entity.Veiculo;
import com.postech.mecanica.domain.enums.StatusOrdemServico;
import com.postech.mecanica.domain.repository.OrdemServicoRepository;
import com.postech.mecanica.domain.repository.VeiculoRepository;
import com.postech.mecanica.exception.ResourceNotFoundException;

@Service
public class CriarOrdemServicoUseCase {

    private final VeiculoRepository veiculoRepository;
    private final OrdemServicoRepository ordemServicoRepository;
    private final OrdemServicoMapper ordemServicoMapper;

    public CriarOrdemServicoUseCase(VeiculoRepository veiculoRepository, OrdemServicoRepository ordemServicoRepository,
            OrdemServicoMapper ordemServicoMapper) {
        this.veiculoRepository = veiculoRepository;
        this.ordemServicoRepository = ordemServicoRepository;
        this.ordemServicoMapper = ordemServicoMapper;
    }

    @Transactional
    public OrdemServicoResponseDTO executar(OrdemServicoRequestDTO requestDTO) {
        Veiculo veiculo = veiculoRepository.consultarPorId(requestDTO.veiculoId())
                .orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado"));

        OrdemServico ordemServico = new OrdemServico(veiculo, StatusOrdemServico.CRIADA, LocalDateTime.now());

        OrdemServico ordemServicoSalvo = ordemServicoRepository.salvar(ordemServico);

        return ordemServicoMapper.toResponseDTO(ordemServicoSalvo);
    }

}
