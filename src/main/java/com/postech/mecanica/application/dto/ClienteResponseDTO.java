package com.postech.mecanica.application.dto;

public record ClienteResponseDTO(
        Long id,
        String nome,
        String cpfCnpj,
        String email,
        String telefone,
        String dataDeNascimento) {
}
