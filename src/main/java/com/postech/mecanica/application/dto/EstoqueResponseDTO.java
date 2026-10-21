package com.postech.mecanica.application.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EstoqueResponseDTO(
        Long id,
        String nomeItem,
        Integer quantidadeItem,
        Double valorItem,
        List<Long> ordensLiberadas) {

    public EstoqueResponseDTO(Long id, String nomeItem, Integer quantidadeItem, Double valorItem) {
        this(id, nomeItem, quantidadeItem, valorItem, null);
    }
}

