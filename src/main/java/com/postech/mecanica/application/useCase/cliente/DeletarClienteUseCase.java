package com.postech.mecanica.application.useCase.cliente;

import org.springframework.stereotype.Component;

import com.postech.mecanica.domain.repository.ClienteRepository;
import com.postech.mecanica.exception.ResourceNotFoundException;

@Component
public class DeletarClienteUseCase {

    private final ClienteRepository clienteRepository;

    public DeletarClienteUseCase(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public void executar(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente não encontrado com o ID: " + id);
        }
        clienteRepository.deletarPorId(id);
    }
}
