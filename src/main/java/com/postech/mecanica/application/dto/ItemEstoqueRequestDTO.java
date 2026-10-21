package com.postech.mecanica.application.dto;

public class ItemEstoqueRequestDTO {

    private Long estoqueId;
    private Integer quantidade;

    public ItemEstoqueRequestDTO() {
    }

    public Long getEstoqueId() {
        return estoqueId;
    }

    public void setEstoqueId(Long estoqueId) {
        this.estoqueId = estoqueId;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

}