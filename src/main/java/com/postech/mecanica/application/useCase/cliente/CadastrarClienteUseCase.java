package com.postech.mecanica.application.useCase.cliente;

import org.springframework.stereotype.Service;

import com.postech.mecanica.application.dto.ClienteVeiculoResponseDTO;
import com.postech.mecanica.application.dto.ClienteVeiculoRequestDTO;
import com.postech.mecanica.application.mapper.ClienteMapper;
import com.postech.mecanica.application.mapper.VeiculoMapper;
import com.postech.mecanica.domain.entity.Cliente;
import com.postech.mecanica.domain.entity.Veiculo;
import com.postech.mecanica.domain.repository.ClienteRepository;
import com.postech.mecanica.domain.repository.VeiculoRepository;

@Service
public class CadastrarClienteUseCase {

    private final VeiculoMapper veiculoMapper;
    private final VeiculoRepository veiculoRepository;
    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    public CadastrarClienteUseCase(VeiculoMapper veiculoMapper, VeiculoRepository veiculoRepository,
            ClienteRepository clienteRepository,
            ClienteMapper clienteMapper) {
        this.veiculoMapper = veiculoMapper;
        this.veiculoRepository = veiculoRepository;
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
    }

    public ClienteVeiculoResponseDTO executar(ClienteVeiculoRequestDTO dto) {
        Veiculo veiculo = veiculoMapper.toDomain(dto.veiculo());
        Veiculo veiculoSalvo = veiculoRepository.salvar(veiculo);

        Cliente cliente = clienteMapper.toDomain(dto, veiculoSalvo);

        Cliente clienteSalvo = clienteRepository.salvar(cliente);
        return clienteMapper.toDTO(clienteSalvo, veiculoSalvo);
    }
}