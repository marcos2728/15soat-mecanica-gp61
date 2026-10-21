package com.postech.mecanica.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VeiculoRequestDTO(
        Long id,
        @NotBlank(message = "Placa não pode ser vazia") String placa,
        @NotBlank(message = "Modelo não pode ser vazio") String modelo,
        @NotBlank(message = "Marca não pode ser vazia") String marca,
        @NotNull(message = "Ano de fabricação não pode ser vazio") Integer anoFabricacao) {
}
