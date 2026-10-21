package com.postech.mecanica.application.dto;

import java.util.List;

public record DiagnosticoConcluidoRequestDTO(
        String descricaoProblema,
        List<ItemEstoqueRequestDTO> pecas,
        List<ItemServicoRequestDTO> servicos) {

    public String getDescricaoProblema() {
        return descricaoProblema;
    }

    public List<ItemEstoqueRequestDTO> getPecas() {
        return pecas;
    }

    public List<ItemServicoRequestDTO> getServicos() {
        return servicos;
    }

}