package com.postech.mecanica.application.dto;

import java.time.LocalDateTime;

public class ApiErrorDTO {

    private int status;
    private String titulo;
    private String mensagem;
    private LocalDateTime dataHora;

    public ApiErrorDTO(int status, String titulo, String mensagem, LocalDateTime dataHora) {
        this.status = status;
        this.titulo = titulo;
        this.mensagem = mensagem;
        this.dataHora = dataHora;
    }

    // getters (sem setters — o objeto é montado só na criação)
    public int getStatus() {
        return status;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }
}