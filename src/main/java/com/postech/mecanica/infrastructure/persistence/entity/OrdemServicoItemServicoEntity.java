package com.postech.mecanica.infrastructure.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ordem_servico_servicos")
public class OrdemServicoItemServicoEntity {

    public OrdemServicoItemServicoEntity() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordem_servico_id", nullable = false)
    private OrdemServicoEntity ordemServico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servico_id", nullable = false)
    private ServicoEntity servico;

    private Double valorAplicado;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrdemServicoEntity getOrdemServico() {
        return ordemServico;
    }

    public void setOrdemServico(OrdemServicoEntity ordemServico) {
        this.ordemServico = ordemServico;
    }

    public ServicoEntity getServico() {
        return servico;
    }

    public void setServico(ServicoEntity servico) {
        this.servico = servico;
    }

    public Double getValorAplicado() {
        return valorAplicado;
    }

    public void setValorAplicado(Double valorAplicado) {
        this.valorAplicado = valorAplicado;
    }

}