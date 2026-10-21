package com.postech.mecanica.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.postech.mecanica.application.dto.OrdemServicoResponseDTO;
import com.postech.mecanica.application.useCase.ordemservico.AprovarOrdemServicoUseCase;
import com.postech.mecanica.application.useCase.ordemservico.RecusarOrdemServicoUseCase;

/**
 * Controller de Interface REST / Webhook mapeado em /api/os/{id}/aprovar e /api/os/{id}/recusar
 * para integração com clientes externos, webhooks e links de notificação.
 */
@RestController
@RequestMapping("/api/os")
public class ApiOsController {

    private final AprovarOrdemServicoUseCase aprovarOrdemServicoUseCase;
    private final RecusarOrdemServicoUseCase recusarOrdemServicoUseCase;

    public ApiOsController(
            AprovarOrdemServicoUseCase aprovarOrdemServicoUseCase,
            RecusarOrdemServicoUseCase recusarOrdemServicoUseCase) {
        this.aprovarOrdemServicoUseCase = aprovarOrdemServicoUseCase;
        this.recusarOrdemServicoUseCase = recusarOrdemServicoUseCase;
    }

    @PostMapping("/{id}/aprovar")
    public ResponseEntity<OrdemServicoResponseDTO> aprovar(@PathVariable Long id) {
        return ResponseEntity.ok(aprovarOrdemServicoUseCase.executar(id));
    }

    @GetMapping("/{id}/aprovar")
    public ResponseEntity<?> aprovarViaGet(
            @PathVariable Long id,
            @RequestHeader(value = "Accept", defaultValue = "") String acceptHeader) {
        OrdemServicoResponseDTO response = aprovarOrdemServicoUseCase.executar(id);
        if (acceptHeader.contains("text/html")) {
            String html = ConfirmationPageRenderer.renderizar(
                    "Orçamento Aprovado com Sucesso!",
                    "A sua Ordem de Serviço foi aprovada e entrará na fila de execução da oficina.",
                    response.status().name(),
                    id,
                    true
            );
            return ResponseEntity.ok().header("Content-Type", "text/html; charset=UTF-8").body(html);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/recusar")
    public ResponseEntity<OrdemServicoResponseDTO> recusar(@PathVariable Long id) {
        return ResponseEntity.ok(recusarOrdemServicoUseCase.executar(id));
    }

    @GetMapping("/{id}/recusar")
    public ResponseEntity<?> recusarViaGet(
            @PathVariable Long id,
            @RequestHeader(value = "Accept", defaultValue = "") String acceptHeader) {
        OrdemServicoResponseDTO response = recusarOrdemServicoUseCase.executar(id);
        if (acceptHeader.contains("text/html")) {
            String html = ConfirmationPageRenderer.renderizar(
                    "Orçamento Recusado",
                    "A sua Ordem de Serviço foi recusada e cancelada conforme sua solicitação.",
                    response.status().name(),
                    id,
                    false
            );
            return ResponseEntity.ok().header("Content-Type", "text/html; charset=UTF-8").body(html);
        }
        return ResponseEntity.ok(response);
    }
}
