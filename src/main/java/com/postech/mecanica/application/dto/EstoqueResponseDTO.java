package com.postech.mecanica.application.dto;

public record EstoqueResponseDTO(
        Long id,
        String nomeItem,
        Integer quantidadeItem,
        Double valorItem) {
}
