package com.postech.mecanica.application.useCase.servico;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postech.mecanica.application.dto.ServicoRequestDTO;
import com.postech.mecanica.application.dto.ServicoResponseDTO;
import com.postech.mecanica.application.mapper.ServicoMapper;
import com.postech.mecanica.domain.entity.Servico;
import com.postech.mecanica.domain.repository.ServicoRepository;
import com.postech.mecanica.exception.ResourceNotFoundException;

@Service
public class AtualizarServicoUseCase {

    private final ServicoRepository servicoRepository;
    private final ServicoMapper servicoMapper;

    public AtualizarServicoUseCase(ServicoRepository servicoRepository, ServicoMapper servicoMapper) {
        this.servicoRepository = servicoRepository;
        this.servicoMapper = servicoMapper;
    }

    @Transactional
    public ServicoResponseDTO executar(Long id, ServicoRequestDTO servicoRequestDTO) {
        Servico servicoExistente = servicoRepository.consultarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado com o ID: " + id));

        servicoExistente.atualizarDados(
                servicoRequestDTO.nomeServico(),
                servicoRequestDTO.valorServico()
        );

        Servico salvo = servicoRepository.salvar(servicoExistente);
        return servicoMapper.toResponseDTO(salvo);
    }
}
