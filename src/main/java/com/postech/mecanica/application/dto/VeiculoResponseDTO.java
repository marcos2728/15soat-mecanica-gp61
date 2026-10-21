package com.postech.mecanica.application.dto;

public record VeiculoResponseDTO(
                Long id,
                String placa,
                String modelo,
                String marca,
                Integer anoFabricacao) {
}
