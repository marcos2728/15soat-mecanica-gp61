package com.postech.mecanica.domain.entity;

import com.postech.mecanica.exception.RegraNegocioException;

public class Estoque {

    private Long id;
    private String nomeItem;
    private Integer quantidadeItem;
    private Double valorItem;

    public Estoque(Long id, String nomeItem, Integer quantidadeItem, Double valorItem) {
        validarDadosObrigatorios(nomeItem, quantidadeItem, valorItem);
        this.id = id;
        this.nomeItem = nomeItem;
        this.quantidadeItem = quantidadeItem;
        this.valorItem = valorItem;
    }

    public Estoque(String nomeItem, Integer quantidadeItem, Double valorItem) {
        this(null, nomeItem, quantidadeItem, valorItem);
    }

    private void validarDadosObrigatorios(String nomeItem, Integer quantidadeItem, Double valorItem) {
        if (nomeItem == null || nomeItem.isBlank()) {
            throw new RegraNegocioException("O nome do item no estoque é obrigatório.");
        }
        if (quantidadeItem == null || quantidadeItem < 0) {
            throw new RegraNegocioException("A quantidade do item no estoque não pode ser negativa ou nula.");
        }
        if (valorItem == null || valorItem < 0.0) {
            throw new RegraNegocioException("O valor do item no estoque não pode ser negativo ou nulo.");
        }
    }

    public void atualizarDados(String nomeItem, Integer quantidadeItem, Double valorItem) {
        validarDadosObrigatorios(nomeItem, quantidadeItem, valorItem);
        this.nomeItem = nomeItem;
        this.quantidadeItem = quantidadeItem;
        this.valorItem = valorItem;
    }

    public boolean temEstoqueSuficiente(Integer quantidade) {
        if (quantidade == null || quantidade <= 0) {
            return false;
        }
        return this.quantidadeItem != null && this.quantidadeItem >= quantidade;
    }

    public void baixarEstoque(Integer quantidade) {
        if (quantidade == null || quantidade <= 0) {
            throw new RegraNegocioException("A quantidade para baixa deve ser maior que zero.");
        }
        if (!temEstoqueSuficiente(quantidade)) {
            throw new RegraNegocioException("Estoque insuficiente para o item: " + this.nomeItem);
        }
        this.quantidadeItem -= quantidade;
    }

    public void adicionarEstoque(Integer quantidade) {
        if (quantidade == null || quantidade <= 0) {
            throw new RegraNegocioException("A quantidade para adicionar ao estoque deve ser maior que zero.");
        }
        this.quantidadeItem = (this.quantidadeItem != null ? this.quantidadeItem : 0) + quantidade;
    }

    public Long getId() {
        return id;
    }

    public String getNomeItem() {
        return nomeItem;
    }

    public Integer getQuantidadeItem() {
        return quantidadeItem;
    }

    public Double getValorItem() {
        return valorItem;
    }
}
