package com.postech.mecanica.application.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.postech.mecanica.application.dto.ClienteResponseDTO;
import com.postech.mecanica.application.dto.ClienteVeiculoRequestDTO;
import com.postech.mecanica.application.dto.ClienteVeiculoResponseDTO;
import com.postech.mecanica.application.dto.VeiculoResponseDTO;
import com.postech.mecanica.domain.entity.Cliente;
import com.postech.mecanica.domain.entity.Veiculo;
import com.postech.mecanica.domain.vo.CpfCnpj;

@Component
public class ClienteMapper {

    private final VeiculoMapper veiculoMapper;

    public ClienteMapper(VeiculoMapper veiculoMapper) {
        this.veiculoMapper = veiculoMapper;
    }

    // Converte DTO -> Domínio
    public Cliente toDomain(ClienteVeiculoRequestDTO dto, Veiculo veiculo) {
        return new Cliente(
                null,
                new CpfCnpj(dto.cpfCnpj()), // Cria o Value Object autovalidável
                dto.nome(),
                dto.email(),
                dto.telefone(),
                dto.dataDeNascimento(),
                veiculo);
    }

    public Cliente toDomain(ClienteVeiculoRequestDTO dto, List<Veiculo> veiculos) {
        return new Cliente(
                null,
                new CpfCnpj(dto.cpfCnpj()),
                dto.nome(),
                dto.email(),
                dto.telefone(),
                dto.dataDeNascimento(),
                veiculos);
    }

    // Converte Domínio -> DTO de Resposta
    public ClienteVeiculoResponseDTO toDTO(Cliente cliente) {
        List<VeiculoResponseDTO> veiculosDTO = cliente.getVeiculos() != null
                ? cliente.getVeiculos().stream()
                        .map(veiculoMapper::toDTO)
                        .toList()
                : List.of();

        return new ClienteVeiculoResponseDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getCpfCnpj().cpfCnpj(),
                cliente.getEmail(),
                cliente.getTelefone(),
                cliente.getDataDeNascimento(),
                veiculosDTO);
    }

    // Sobrecarga para compatibilidade com chamadas que passam veiculo
    public ClienteVeiculoResponseDTO toDTO(Cliente cliente, Veiculo veiculo) {
        return toDTO(cliente);
    }

    // Converte Domínio -> DTO de Resposta apenas com dados do cliente
    public ClienteResponseDTO toClienteResponseDTO(Cliente cliente) {
        if (cliente == null) {
            return null;
        }
        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getCpfCnpj() != null ? cliente.getCpfCnpj().cpfCnpj() : null,
                cliente.getEmail(),
                cliente.getTelefone(),
                cliente.getDataDeNascimento());
    }
}
