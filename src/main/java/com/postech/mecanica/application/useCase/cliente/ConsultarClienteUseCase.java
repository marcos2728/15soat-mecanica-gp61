package com.postech.mecanica.application.useCase.cliente;

import org.springframework.stereotype.Service;

import com.postech.mecanica.application.dto.ClienteVeiculoResponseDTO;
import com.postech.mecanica.application.mapper.ClienteMapper;
import com.postech.mecanica.domain.entity.Cliente;
import com.postech.mecanica.domain.repository.ClienteRepository;
import com.postech.mecanica.exception.ResourceNotFoundException;

@Service
public class ConsultarClienteUseCase {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    public ConsultarClienteUseCase(ClienteRepository clienteRepository, ClienteMapper clienteMapper) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
    }

    public ClienteVeiculoResponseDTO executar(String documentoCliente) {
        Cliente cliente = clienteRepository.consultarDocumentoCliente(documentoCliente)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado: " + documentoCliente));
        return clienteMapper.toDTO(cliente);
    }
}