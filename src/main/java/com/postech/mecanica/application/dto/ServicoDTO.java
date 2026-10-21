package com.postech.mecanica.application.dto;

public class ServicoDTO {
    private Long id;
    private String nomeServico;
    private Double valorServico;

    public ServicoDTO() {
    }

    public ServicoDTO(Long id, String nomeServico, Double valorServico) {
        this.id = id;
        this.nomeServico = nomeServico;
        this.valorServico = valorServico;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeServico() {
        return nomeServico;
    }

    public void setNomeServico(String nomeServico) {
        this.nomeServico = nomeServico;
    }

    public Double getValorServico() {
        return valorServico;
    }

    public void setValorServico(Double valorServico) {
        this.valorServico = valorServico;
    }

}
