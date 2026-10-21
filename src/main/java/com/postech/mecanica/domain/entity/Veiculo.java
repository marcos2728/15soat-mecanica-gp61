package com.postech.mecanica.domain.entity;

import com.postech.mecanica.domain.vo.Placa;
import com.postech.mecanica.exception.RegraNegocioException;

public class Veiculo {

    private Long id;
    private Placa placa;
    private String modelo;
    private String marca;
    private Integer anoFabricacao;

    public Veiculo(Long id, Placa placa, String modelo, String marca, Integer anoFabricacao) {
        validarDadosObrigatorios(placa, modelo, marca, anoFabricacao);
        this.id = id;
        this.placa = placa;
        this.modelo = modelo;
        this.marca = marca;
        this.anoFabricacao = anoFabricacao;
    }

    private void validarDadosObrigatorios(Placa placa, String modelo, String marca, Integer anoFabricacao) {
        if (placa == null) {
            throw new RegraNegocioException("A placa do veículo é obrigatória.");
        }
        if (modelo == null || modelo.isBlank()) {
            throw new RegraNegocioException("O modelo do veículo é obrigatório.");
        }
        if (marca == null || marca.isBlank()) {
            throw new RegraNegocioException("A marca do veículo é obrigatória.");
        }
        if (anoFabricacao == null || anoFabricacao < 1900) {
            throw new RegraNegocioException("Ano de fabricação inválido.");
        }
    }

    public void atualizarDados(String modelo, String marca, Integer anoFabricacao) {
        if (modelo != null && !modelo.isBlank()) {
            this.modelo = modelo;
        }
        if (marca != null && !marca.isBlank()) {
            this.marca = marca;
        }
        if (anoFabricacao != null && anoFabricacao >= 1900) {
            this.anoFabricacao = anoFabricacao;
        }
    }

    public void atualizarPlaca(Placa novaPlaca) {
        if (novaPlaca == null) {
            throw new RegraNegocioException("A nova placa não pode ser nula.");
        }
        this.placa = novaPlaca;
    }

    public Long getId() {
        return id;
    }

    public Placa getPlaca() {
        return placa;
    }

    public String getModelo() {
        return modelo;
    }

    public String getMarca() {
        return marca;
    }

    public Integer getAnoFabricacao() {
        return anoFabricacao;
    }
}
