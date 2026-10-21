package com.postech.mecanica.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ServicoRequestDTO(
        @NotBlank(message = "O nome do serviço é obrigatório")
        String nomeServico,

        @NotNull(message = "O valor do serviço é obrigatório")
        @PositiveOrZero(message = "O valor do serviço não pode ser negativo")
        Double valorServico) {
}
