package com.postech.mecanica.application.useCase.cliente;

import org.springframework.stereotype.Service;

import com.postech.mecanica.application.dto.ClienteRequestDTO;
import com.postech.mecanica.application.dto.ClienteResponseDTO;
import com.postech.mecanica.application.mapper.ClienteMapper;
import com.postech.mecanica.domain.entity.Cliente;
import com.postech.mecanica.domain.repository.ClienteRepository;
import com.postech.mecanica.exception.ResourceNotFoundException;

@Service
public class AtualizarClienteUseCase {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    public AtualizarClienteUseCase(ClienteRepository clienteRepository, ClienteMapper clienteMapper) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
    }

    public ClienteResponseDTO executar(Long id, ClienteRequestDTO clienteDTO) {
        Cliente clienteExistente = clienteRepository.consultarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com o ID: " + id));

        // Atualiza apenas os campos que podem ser editados

        clienteExistente.atualizarDados(
                clienteDTO.nome(),
                clienteDTO.dataDeNascimento(),
                clienteDTO.email(),
                clienteDTO.telefone());
        Cliente clienteSalvo = clienteRepository.salvar(clienteExistente);
        return clienteMapper.toClienteResponseDTO(clienteSalvo);
    }
}