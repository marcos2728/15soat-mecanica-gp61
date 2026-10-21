package com.postech.mecanica.application.useCase.ordemservico;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.postech.mecanica.domain.event.OrdemServicoAguardandoAprovacaoEvent;

/**
 * Ouvinte de eventos responsável por processar o envio de notificação
 * quando uma OS entra em status de aguardando aprovação do cliente.
 */
@Component
public class OrdemServicoNotificacaoListener {

    private static final Logger log = LoggerFactory.getLogger(OrdemServicoNotificacaoListener.class);

    private final NotificarClienteAprovacaoUseCase notificarClienteAprovacaoUseCase;

    public OrdemServicoNotificacaoListener(NotificarClienteAprovacaoUseCase notificarClienteAprovacaoUseCase) {
        this.notificarClienteAprovacaoUseCase = notificarClienteAprovacaoUseCase;
    }

    @EventListener
    public void aoAguardarAprovacao(OrdemServicoAguardandoAprovacaoEvent evento) {
        if (evento == null || evento.getOrdemServicoId() == null) {
            return;
        }

        log.info("Processando evento de aguardando aprovação para OS #{}", evento.getOrdemServicoId());
        try {
            notificarClienteAprovacaoUseCase.executar(evento.getOrdemServicoId());
        } catch (Exception e) {
            log.error("Erro ao processar notificação de aprovação para OS #{}: {}",
                    evento.getOrdemServicoId(), e.getMessage(), e);
        }
    }
}
