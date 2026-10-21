package com.postech.mecanica.application.dto;

import java.util.List;

public record ClienteVeiculoResponseDTO(
        Long id,
        String nome,
        String cpfCnpj,
        String email,
        String telefone,
        String dataDeNascimento,
        List<VeiculoResponseDTO> veiculos) {
}
