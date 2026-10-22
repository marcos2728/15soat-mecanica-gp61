package com.postech.mecanica.domain.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StatusOrdemServicoTest {

    @Test
    @DisplayName("Deve validar todas as transições de status permitidas")
    void deveValidarTransicoesPermitidas() {
        // CRIADA -> EM_DIAGNOSTICO, CANCELADA
        assertTrue(StatusOrdemServico.CRIADA.podeTransitarPara(StatusOrdemServico.EM_DIAGNOSTICO));
        assertTrue(StatusOrdemServico.CRIADA.podeTransitarPara(StatusOrdemServico.CANCELADA));
        assertFalse(StatusOrdemServico.CRIADA.podeTransitarPara(StatusOrdemServico.FINALIZADA));

        // EM_DIAGNOSTICO -> DIAGNOSTICO_CONCLUIDO, PENDENTE_ESTOQUE, CANCELADA
        assertTrue(StatusOrdemServico.EM_DIAGNOSTICO.podeTransitarPara(StatusOrdemServico.DIAGNOSTICO_CONCLUIDO));
        assertTrue(StatusOrdemServico.EM_DIAGNOSTICO.podeTransitarPara(StatusOrdemServico.PENDENTE_ESTOQUE));
        assertTrue(StatusOrdemServico.EM_DIAGNOSTICO.podeTransitarPara(StatusOrdemServico.CANCELADA));

        // PENDENTE_ESTOQUE -> DIAGNOSTICO_CONCLUIDO, CANCELADA
        assertTrue(StatusOrdemServico.PENDENTE_ESTOQUE.podeTransitarPara(StatusOrdemServico.DIAGNOSTICO_CONCLUIDO));
        assertTrue(StatusOrdemServico.PENDENTE_ESTOQUE.podeTransitarPara(StatusOrdemServico.CANCELADA));

        // DIAGNOSTICO_CONCLUIDO -> PENDENTE_NOTIFICACAO_CLIENTE, PENDENTE_APROVACAO_CLIENTE, CANCELADA
        assertTrue(StatusOrdemServico.DIAGNOSTICO_CONCLUIDO.podeTransitarPara(StatusOrdemServico.PENDENTE_NOTIFICACAO_CLIENTE));
        assertTrue(StatusOrdemServico.DIAGNOSTICO_CONCLUIDO.podeTransitarPara(StatusOrdemServico.PENDENTE_APROVACAO_CLIENTE));
        assertTrue(StatusOrdemServico.DIAGNOSTICO_CONCLUIDO.podeTransitarPara(StatusOrdemServico.CANCELADA));

        // PENDENTE_NOTIFICACAO_CLIENTE -> PENDENTE_APROVACAO_CLIENTE, APROVADA, CANCELADA
        assertTrue(StatusOrdemServico.PENDENTE_NOTIFICACAO_CLIENTE.podeTransitarPara(StatusOrdemServico.PENDENTE_APROVACAO_CLIENTE));
        assertTrue(StatusOrdemServico.PENDENTE_NOTIFICACAO_CLIENTE.podeTransitarPara(StatusOrdemServico.APROVADA));
        assertTrue(StatusOrdemServico.PENDENTE_NOTIFICACAO_CLIENTE.podeTransitarPara(StatusOrdemServico.CANCELADA));

        // PENDENTE_APROVACAO_CLIENTE -> APROVADA, CANCELADA
        assertTrue(StatusOrdemServico.PENDENTE_APROVACAO_CLIENTE.podeTransitarPara(StatusOrdemServico.APROVADA));
        assertTrue(StatusOrdemServico.PENDENTE_APROVACAO_CLIENTE.podeTransitarPara(StatusOrdemServico.CANCELADA));

        // APROVADA -> EM_EXECUCAO, CANCELADA
        assertTrue(StatusOrdemServico.APROVADA.podeTransitarPara(StatusOrdemServico.EM_EXECUCAO));
        assertTrue(StatusOrdemServico.APROVADA.podeTransitarPara(StatusOrdemServico.CANCELADA));

        // EM_EXECUCAO -> FINALIZADA, CANCELADA
        assertTrue(StatusOrdemServico.EM_EXECUCAO.podeTransitarPara(StatusOrdemServico.FINALIZADA));
        assertTrue(StatusOrdemServico.EM_EXECUCAO.podeTransitarPara(StatusOrdemServico.CANCELADA));

        // FINALIZADA -> PENDENTE_ENTREGA, CANCELADA
        assertTrue(StatusOrdemServico.FINALIZADA.podeTransitarPara(StatusOrdemServico.PENDENTE_ENTREGA));
        assertTrue(StatusOrdemServico.FINALIZADA.podeTransitarPara(StatusOrdemServico.CANCELADA));

        // PENDENTE_ENTREGA -> ENTREGUE, CANCELADA
        assertTrue(StatusOrdemServico.PENDENTE_ENTREGA.podeTransitarPara(StatusOrdemServico.ENTREGUE));
        assertTrue(StatusOrdemServico.PENDENTE_ENTREGA.podeTransitarPara(StatusOrdemServico.CANCELADA));

        // ENTREGUE e CANCELADA -> vazios
        assertEquals(Set.of(), StatusOrdemServico.ENTREGUE.proximosStatusPermitidos());
        assertEquals(Set.of(), StatusOrdemServico.CANCELADA.proximosStatusPermitidos());
    }

    @Test
    @DisplayName("Deve verificar enum PerfilUsuario")
    void deveVerificarPerfilUsuario() {
        assertNotNull(PerfilUsuario.valueOf("GERENTE"));
        assertNotNull(PerfilUsuario.valueOf("MECANICO"));
        assertEquals(2, PerfilUsuario.values().length);
    }
}
