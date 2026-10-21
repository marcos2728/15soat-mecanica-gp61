package com.postech.mecanica.application.useCase.ordemservico;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postech.mecanica.application.dto.OrdemServicoResponseDTO;
import com.postech.mecanica.application.mapper.OrdemServicoMapper;
import com.postech.mecanica.domain.entity.Cliente;
import com.postech.mecanica.domain.repository.ClienteRepository;
import com.postech.mecanica.domain.repository.OrdemServicoRepository;
import com.postech.mecanica.exception.ResourceNotFoundException;

@Service
public class ConsultarOrdemClienteUseCase {

    private final ClienteRepository clienteRepository;
    private final OrdemServicoRepository ordemServicoRepository;
    private final OrdemServicoMapper ordemServicoMapper;

    public ConsultarOrdemClienteUseCase(ClienteRepository clienteRepository,
            OrdemServicoRepository ordemServicoRepository,
            OrdemServicoMapper ordemServicoMapper) {
        this.clienteRepository = clienteRepository;
        this.ordemServicoRepository = ordemServicoRepository;
        this.ordemServicoMapper = ordemServicoMapper;
    }

    @Transactional(readOnly = true)
    public List<OrdemServicoResponseDTO> executar(String documentoCliente) {
        Cliente cliente = clienteRepository.consultarDocumentoCliente(documentoCliente)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com o documento: " + documentoCliente));

        return cliente.getVeiculos().stream()
                .flatMap(veiculo -> ordemServicoRepository.buscarPorVeiculoId(veiculo.getId()).stream())
                .map(ordemServicoMapper::toResponseDTO)
                .toList();
    }
}
