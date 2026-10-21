package com.postech.mecanica.domain.service;

import com.postech.mecanica.domain.entity.Cliente;
import com.postech.mecanica.domain.entity.OrdemServico;

/**
 * Contrato da Camada de Domínio para serviços de notificação por e-mail.
 * Define as operações essenciais sem acoplamento a implementações ou frameworks externos.
 */
public interface IEmailService {

    /**
     * Envia notificação de orçamento da Ordem de Serviço para o cliente,
     * incluindo detalhamento de itens e links de aprovação/recusa.
     *
     * @param cliente      Cliente destinatário da notificação
     * @param ordemServico Ordem de serviço com peças, serviços e valor total
     * @param linkAprovar  URL para aprovação direta do orçamento
     * @param linkRecusar  URL para recusa direta do orçamento
     */
    void enviarNotificacaoOrcamento(Cliente cliente, OrdemServico ordemServico, String linkAprovar, String linkRecusar);

    /**
     * Envia um e-mail com conteúdo HTML para um destinatário específico.
     *
     * @param destinatario Endereço de e-mail de destino
     * @param assunto      Assunto da mensagem
     * @param corpoHtml    Corpo da mensagem em formato HTML
     */
    void enviarEmail(String destinatario, String assunto, String corpoHtml);
}
