package com.postech.mecanica.presentation.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ConfirmationPageRendererTest {

    @Test
    @DisplayName("Deve renderizar página HTML de sucesso com cores e ícones corretos")
    void deveRenderizarPaginaDeSucesso() {
        String html = ConfirmationPageRenderer.renderizar(
                "Aprovado!",
                "OS aprovada",
                "APROVADA",
                1L,
                true
        );

        assertNotNull(html);
        assertTrue(html.contains("Aprovado!"));
        assertTrue(html.contains("#16a34a"));
        assertTrue(html.contains("✓"));
        assertTrue(html.contains("Ordem de Serviço #1 • Status: APROVADA"));
    }

    @Test
    @DisplayName("Deve renderizar página HTML de recusa com cores e ícones corretos")
    void deveRenderizarPaginaDeRecusa() {
        String html = ConfirmationPageRenderer.renderizar(
                "Recusado!",
                "OS recusada",
                "CANCELADA",
                2L,
                false
        );

        assertNotNull(html);
        assertTrue(html.contains("Recusado!"));
        assertTrue(html.contains("#dc2626"));
        assertTrue(html.contains("✕"));
        assertTrue(html.contains("Ordem de Serviço #2 • Status: CANCELADA"));
    }
}
