package com.postech.mecanica.domain.entity;

import com.postech.mecanica.exception.RegraNegocioException;

public class OrdemServicoItemServico {

    private Long id;
    private Long servicoId;
    private String nomeServico;
    private Double valorAplicado;

    public OrdemServicoItemServico(Long id, Long servicoId, String nomeServico, Double valorAplicado) {
        validarDadosObrigatorios(servicoId, valorAplicado);
        this.id = id;
        this.servicoId = servicoId;
        this.nomeServico = nomeServico;
        this.valorAplicado = valorAplicado != null ? valorAplicado : 0.0;
    }

    public OrdemServicoItemServico(Long servicoId, Double valorAplicado) {
        this(null, servicoId, null, valorAplicado);
    }

    public OrdemServicoItemServico(Long servicoId, String nomeServico, Double valorAplicado) {
        this(null, servicoId, nomeServico, valorAplicado);
    }

    private void validarDadosObrigatorios(Long servicoId, Double valorAplicado) {
        if (servicoId == null) {
            throw new RegraNegocioException("O ID do serviço é obrigatório.");
        }
        if (valorAplicado == null || valorAplicado < 0) {
            throw new RegraNegocioException("O valor aplicado do serviço não pode ser negativo.");
        }
    }

    public Long getId() {
        return id;
    }

    public Long getServicoId() {
        return servicoId;
    }

    public String getNomeServico() {
        return nomeServico;
    }

    public Double getValorAplicado() {
        return valorAplicado;
    }
}
