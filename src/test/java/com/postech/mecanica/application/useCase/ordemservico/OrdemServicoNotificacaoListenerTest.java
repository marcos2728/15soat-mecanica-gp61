package com.postech.mecanica.application.useCase.ordemservico;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.postech.mecanica.domain.event.OrdemServicoAguardandoAprovacaoEvent;

@ExtendWith(MockitoExtension.class)
class OrdemServicoNotificacaoListenerTest {

    @Mock
    private NotificarClienteAprovacaoUseCase notificarClienteAprovacaoUseCase;

    @InjectMocks
    private OrdemServicoNotificacaoListener listener;

    @Test
    @DisplayName("Deve delegar notificação ao use case com sucesso")
    void deveDelegarNotificacaoComSucesso() {
        OrdemServicoAguardandoAprovacaoEvent evento = new OrdemServicoAguardandoAprovacaoEvent(1L);

        listener.aoAguardarAprovacao(evento);

        verify(notificarClienteAprovacaoUseCase).executar(1L);
    }

    @Test
    @DisplayName("Deve ignorar evento nulo ou com ID nulo")
    void deveIgnorarEventoNuloOuComIdNulo() {
        listener.aoAguardarAprovacao(null);
        listener.aoAguardarAprovacao(new OrdemServicoAguardandoAprovacaoEvent(null));

        verify(notificarClienteAprovacaoUseCase, never()).executar(any());
    }

    @Test
    @DisplayName("Deve capturar e tratar exceção sem propagar erro")
    void deveTratarExcecaoSemPropagar() {
        OrdemServicoAguardandoAprovacaoEvent evento = new OrdemServicoAguardandoAprovacaoEvent(1L);
        doThrow(new RuntimeException("Erro SMTP")).when(notificarClienteAprovacaoUseCase).executar(1L);

        assertDoesNotThrow(() -> listener.aoAguardarAprovacao(evento));
        verify(notificarClienteAprovacaoUseCase).executar(1L);
    }
}
