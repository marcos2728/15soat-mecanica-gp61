package com.postech.mecanica.application.dto;

/**
 * @deprecated Utilizar {@link ServicoRequestDTO} para requisições e {@link ServicoResponseDTO} para respostas.
 */
@Deprecated
public record ServicoDTO(
        Long id,
        String nomeServico,
        Double valorServico) {

    public ServicoDTO(String nomeServico, Double valorServico) {
        this(null, nomeServico, valorServico);
    }
}
