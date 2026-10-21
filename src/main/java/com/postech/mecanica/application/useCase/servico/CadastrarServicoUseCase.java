package com.postech.mecanica.application.useCase.servico;

import org.springframework.stereotype.Service;

import com.postech.mecanica.application.dto.ServicoRequestDTO;
import com.postech.mecanica.application.dto.ServicoResponseDTO;
import com.postech.mecanica.application.mapper.ServicoMapper;
import com.postech.mecanica.domain.entity.Servico;
import com.postech.mecanica.domain.repository.ServicoRepository;

@Service
public class CadastrarServicoUseCase {

    private final ServicoRepository servicoRepository;
    private final ServicoMapper servicoMapper;

    public CadastrarServicoUseCase(ServicoRepository servicoRepository, ServicoMapper servicoMapper) {
        this.servicoRepository = servicoRepository;
        this.servicoMapper = servicoMapper;
    }

    public ServicoResponseDTO executar(ServicoRequestDTO servicoDTO) {
        Servico servico = servicoMapper.toDomain(servicoDTO);
        return servicoMapper.toResponseDTO(servicoRepository.salvar(servico));
    }
}
