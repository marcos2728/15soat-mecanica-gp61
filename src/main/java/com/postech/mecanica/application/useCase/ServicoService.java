package com.postech.mecanica.application.useCase;

import java.util.List;

import org.springframework.stereotype.Service;

import com.postech.mecanica.application.dto.ServicoRequestDTO;
import com.postech.mecanica.application.dto.ServicoResponseDTO;
import com.postech.mecanica.application.mapper.ServicoMapper;
import com.postech.mecanica.domain.entity.Servico;
import com.postech.mecanica.domain.repository.ServicoRepository;
import com.postech.mecanica.exception.ResourceNotFoundException;

@Service
public class ServicoService {

    private final ServicoRepository servicoRepository;
    private final ServicoMapper servicoMapper;

    public ServicoService(ServicoRepository servicoRepository, ServicoMapper servicoMapper) {
        this.servicoRepository = servicoRepository;
        this.servicoMapper = servicoMapper;
    }

    public ServicoResponseDTO salvar(ServicoRequestDTO servicoDTO) {
        Servico servico = servicoMapper.toDomain(servicoDTO);
        return servicoMapper.toResponseDTO(servicoRepository.salvar(servico));
    }

    public List<ServicoResponseDTO> listarTodos() {
        return servicoRepository.consultarTodos().stream()
                .map(servicoMapper::toResponseDTO)
                .toList();
    }

    public ServicoResponseDTO ServicoPorId(Long id) {
        return servicoRepository.consultarPorId(id)
                .map(servicoMapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado com o ID: " + id));
    }

    public ServicoResponseDTO atualizar(Long id, ServicoRequestDTO servicoDTO) {
        Servico servicoExistente = servicoRepository.consultarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado com o ID: " + id));

        servicoExistente.atualizarDados(servicoDTO.nomeServico(), servicoDTO.valorServico());
        return servicoMapper.toResponseDTO(servicoRepository.salvar(servicoExistente));
    }

    public void deletar(Long id) {
        if (!servicoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Serviço não encontrado com o ID: " + id);
        }
        servicoRepository.deletarPorId(id);
    }
}
