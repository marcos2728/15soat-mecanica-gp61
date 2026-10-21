package com.postech.mecanica.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record EstoqueRequestDTO(
        @NotBlank(message = "O nome do item é obrigatório")
        String nomeItem,

        @NotNull(message = "A quantidade do item é obrigatória")
        @PositiveOrZero(message = "A quantidade do item não pode ser negativa")
        Integer quantidadeItem,

        @NotNull(message = "O valor do item é obrigatório")
        @PositiveOrZero(message = "O valor do item não pode ser negativo")
        Double valorItem) {
}
