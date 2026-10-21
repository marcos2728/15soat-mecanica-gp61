package com.postech.mecanica.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ClienteRequestDTO(
        @NotBlank(message = "O nome é obrigatório") String nome,
        @NotBlank(message = "O e-mail é obrigatório") @Email(message = "E-mail com formato inválido") String email,
        @NotBlank(message = "O telefone é obrigatório") String telefone,
        String dataDeNascimento) {
}
