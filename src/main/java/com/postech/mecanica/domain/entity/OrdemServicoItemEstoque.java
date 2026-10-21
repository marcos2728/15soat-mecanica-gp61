package com.postech.mecanica.domain.entity;

import com.postech.mecanica.exception.RegraNegocioException;

public class OrdemServicoItemEstoque {

    private Long id;
    private Long estoqueId;
    private String nomePeca;
    private Integer quantidade;
    private Double valorUnitario;
    private Boolean disponivel;

    public OrdemServicoItemEstoque(Long id, Long estoqueId, String nomePeca, Integer quantidade, Double valorUnitario,
            Boolean disponivel) {
        validarDadosObrigatorios(estoqueId, quantidade);
        this.id = id;
        this.estoqueId = estoqueId;
        this.nomePeca = nomePeca;
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario != null ? valorUnitario : 0.0;
        this.disponivel = disponivel != null ? disponivel : false;
    }

    public OrdemServicoItemEstoque(Long estoqueId, Integer quantidade, Double valorUnitario, Boolean disponivel) {
        this(null, estoqueId, null, quantidade, valorUnitario, disponivel);
    }

    public OrdemServicoItemEstoque(Long estoqueId, String nomePeca, Integer quantidade, Double valorUnitario,
            Boolean disponivel) {
        this(null, estoqueId, nomePeca, quantidade, valorUnitario, disponivel);
    }

    private void validarDadosObrigatorios(Long estoqueId, Integer quantidade) {
        if (estoqueId == null) {
            throw new RegraNegocioException("O ID da peça/estoque é obrigatório.");
        }
        if (quantidade == null || quantidade <= 0) {
            throw new RegraNegocioException("A quantidade da peça deve ser maior que zero.");
        }
    }

    public Double getValorTotal() {
        return (quantidade != null && valorUnitario != null) ? quantidade * valorUnitario : 0.0;
    }

    public void marcarComoDisponivel(Double valorUnitario) {
        this.disponivel = true;
        if (valorUnitario != null) {
            this.valorUnitario = valorUnitario;
        }
    }

    public void marcarComoIndisponivel() {
        this.disponivel = false;
    }

    public Long getId() {
        return id;
    }

    public Long getEstoqueId() {
        return estoqueId;
    }

    public String getNomePeca() {
        return nomePeca;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public Double getValorUnitario() {
        return valorUnitario;
    }

    public Boolean getDisponivel() {
        return disponivel;
    }
}
