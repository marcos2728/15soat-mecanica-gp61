package com.postech.mecanica.application.useCase.veiculo;

import java.util.List;

import org.springframework.stereotype.Service;

import com.postech.mecanica.application.dto.VeiculoResponseDTO;
import com.postech.mecanica.application.mapper.VeiculoMapper;
import com.postech.mecanica.domain.entity.Cliente;
import com.postech.mecanica.domain.repository.ClienteRepository;
import com.postech.mecanica.exception.ResourceNotFoundException;

@Service
public class ConsultarVeiculosClienteUseCase {

    private final ClienteRepository clienteRepository;
    private final VeiculoMapper veiculoMapper;

    public ConsultarVeiculosClienteUseCase(ClienteRepository clienteRepository, VeiculoMapper veiculoMapper) {
        this.clienteRepository = clienteRepository;
        this.veiculoMapper = veiculoMapper;
    }

    public List<VeiculoResponseDTO> executar(String documentoCliente) {
        Cliente cliente = clienteRepository.consultarDocumentoCliente(documentoCliente)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com o documento: " + documentoCliente));

        return cliente.getVeiculos().stream()
                .map(veiculoMapper::toDTO)
                .toList();
    }
}
