package com.postech.mecanica.domain.entity;

import com.postech.mecanica.exception.RegraNegocioException;

public class Servico {

    private Long id;
    private String nomeServico;
    private Double valorServico;

    public Servico(Long id, String nomeServico, Double valorServico) {
        validarDadosObrigatorios(nomeServico, valorServico);
        this.id = id;
        this.nomeServico = nomeServico;
        this.valorServico = valorServico;
    }

    public Servico(String nomeServico, Double valorServico) {
        this(null, nomeServico, valorServico);
    }

    private void validarDadosObrigatorios(String nomeServico, Double valorServico) {
        if (nomeServico == null || nomeServico.isBlank()) {
            throw new RegraNegocioException("O nome do serviço é obrigatório.");
        }
        if (valorServico == null || valorServico < 0.0) {
            throw new RegraNegocioException("O valor do serviço não pode ser negativo ou nulo.");
        }
    }

    public void atualizarDados(String nomeServico, Double valorServico) {
        validarDadosObrigatorios(nomeServico, valorServico);
        this.nomeServico = nomeServico;
        this.valorServico = valorServico;
    }

    public Long getId() {
        return id;
    }

    public String getNomeServico() {
        return nomeServico;
    }

    public Double getValorServico() {
        return valorServico;
    }
}
