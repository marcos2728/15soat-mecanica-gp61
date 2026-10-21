package com.postech.mecanica.application.dto;

public record EstoqueDTO(
        Long id,
        String nomeItem,
        Integer quantidadeItem,
        Double valorItem) {
}
