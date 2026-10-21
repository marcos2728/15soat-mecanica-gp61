package com.postech.mecanica.application.useCase.veiculo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postech.mecanica.application.dto.VeiculoRequestDTO;
import com.postech.mecanica.application.dto.VeiculoResponseDTO;
import com.postech.mecanica.application.mapper.VeiculoMapper;
import com.postech.mecanica.domain.entity.Cliente;
import com.postech.mecanica.domain.entity.Veiculo;
import com.postech.mecanica.domain.repository.ClienteRepository;
import com.postech.mecanica.domain.repository.VeiculoRepository;
import com.postech.mecanica.exception.ResourceNotFoundException;

@Service
public class CadastrarVeiculoUseCase {

    private final VeiculoMapper veiculoMapper;
    private final VeiculoRepository veiculoRepository;
    private final ClienteRepository clienteRepository;

    public CadastrarVeiculoUseCase(VeiculoMapper veiculoMapper, VeiculoRepository veiculoRepository,
            ClienteRepository clienteRepository) {
        this.veiculoMapper = veiculoMapper;
        this.veiculoRepository = veiculoRepository;
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public VeiculoResponseDTO executar(Long idCliente, VeiculoRequestDTO veiculoDTO) {
        Cliente cliente = clienteRepository.consultarPorId(idCliente)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com o id: " + idCliente));

        Veiculo veiculo = veiculoMapper.toDomain(veiculoDTO);
        Veiculo veiculoSalvo = veiculoRepository.salvar(veiculo);

        cliente.adicionarVeiculo(veiculoSalvo);
        clienteRepository.salvar(cliente);

        return veiculoMapper.toDTO(veiculoSalvo);
    }
}
