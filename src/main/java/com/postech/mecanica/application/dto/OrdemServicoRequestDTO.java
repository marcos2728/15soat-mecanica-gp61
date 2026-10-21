package com.postech.mecanica.application.dto;

import jakarta.validation.constraints.NotNull;

public record OrdemServicoRequestDTO(
        @NotNull(message = "O ID do veículo é obrigatório") Long veiculoId) {
}