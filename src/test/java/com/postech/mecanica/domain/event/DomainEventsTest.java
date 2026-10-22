package com.postech.mecanica.domain.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DomainEventsTest {

    @Test
    @DisplayName("Deve registrar ordens liberadas no EstoqueReabastecidoEvent sem duplicatas ou nulos")
    void deveGerenciarEstoqueReabastecidoEvent() {
        EstoqueReabastecidoEvent evento = new EstoqueReabastecidoEvent(10L);
        assertEquals(10L, evento.getEstoqueId());
        assertTrue(evento.getOrdensServicoLiberadas().isEmpty());

        evento.registrarOrdemLiberada(1L);
        evento.registrarOrdemLiberada(2L);
        evento.registrarOrdemLiberada(1L); // duplicada, não deve duplicar
        evento.registrarOrdemLiberada(null); // nulo, deve ser ignorado

        List<Long> liberadas = evento.getOrdensServicoLiberadas();
        assertEquals(2, liberadas.size());
        assertEquals(List.of(1L, 2L), liberadas);

        // Testa imutabilidade da lista retornada
        assertThrows(UnsupportedOperationException.class, () -> liberadas.add(3L));
    }

    @Test
    @DisplayName("Deve instanciar e retornar ID em OrdemServicoAguardandoAprovacaoEvent")
    void deveInstanciarOrdemServicoAguardandoAprovacaoEvent() {
        OrdemServicoAguardandoAprovacaoEvent evento = new OrdemServicoAguardandoAprovacaoEvent(99L);
        assertNotNull(evento);
        assertEquals(99L, evento.getOrdemServicoId());
    }
}
