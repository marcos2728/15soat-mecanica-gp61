package com.postech.mecanica.presentation.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.postech.mecanica.application.dto.AtualizarStatusOrdemServicoRequestDTO;
import com.postech.mecanica.application.dto.DiagnosticoConcluidoRequestDTO;
import com.postech.mecanica.application.dto.OrdemServicoRequestDTO;
import com.postech.mecanica.application.dto.OrdemServicoResponseDTO;
import com.postech.mecanica.application.dto.TempoMedioPorFaseResponseDTO;
import com.postech.mecanica.application.dto.TempoPorFaseResponseDTO;
import com.postech.mecanica.application.useCase.ordemservico.AprovarOrdemServicoUseCase;
import com.postech.mecanica.application.useCase.ordemservico.AtualizarDiagnosticoConlcuidoUseCase;
import com.postech.mecanica.application.useCase.ordemservico.AtualizarStatusOrdemUseCase;
import com.postech.mecanica.application.useCase.ordemservico.BuscarTemposPorOrdemServicoUseCase;
import com.postech.mecanica.application.useCase.ordemservico.CalcularTempoMedioPorFaseUseCase;
import com.postech.mecanica.application.useCase.ordemservico.ConsultarOrdemClienteUseCase;
import com.postech.mecanica.application.useCase.ordemservico.ConsultarOrdemServicoUseCase;
import com.postech.mecanica.application.useCase.ordemservico.CriarOrdemServicoUseCase;
import com.postech.mecanica.application.useCase.ordemservico.RecusarOrdemServicoUseCase;

@RestController
@RequestMapping("/ordens-servicos")
public class OrdemServicoController {

    private final CriarOrdemServicoUseCase criarOrdemServicoUseCase;
    private final ConsultarOrdemServicoUseCase consultarOrdemServicoUseCase;
    private final ConsultarOrdemClienteUseCase consultarOrdemClienteUseCase;
    private final AtualizarStatusOrdemUseCase atualizarStatusOrdemUseCase;
    private final AtualizarDiagnosticoConlcuidoUseCase atualizarDiagnosticoConlcuidoUseCase;
    private final BuscarTemposPorOrdemServicoUseCase buscarTemposPorOrdemServicoUseCase;
    private final CalcularTempoMedioPorFaseUseCase calcularTempoMedioPorFaseUseCase;
    private final AprovarOrdemServicoUseCase aprovarOrdemServicoUseCase;
    private final RecusarOrdemServicoUseCase recusarOrdemServicoUseCase;

    public OrdemServicoController(
            CriarOrdemServicoUseCase criarOrdemServicoUseCase,
            ConsultarOrdemServicoUseCase consultarOrdemServicoUseCase,
            ConsultarOrdemClienteUseCase consultarOrdemClienteUseCase,
            AtualizarStatusOrdemUseCase atualizarStatusOrdemUseCase,
            AtualizarDiagnosticoConlcuidoUseCase atualizarDiagnosticoConlcuidoUseCase,
            BuscarTemposPorOrdemServicoUseCase buscarTemposPorOrdemServicoUseCase,
            CalcularTempoMedioPorFaseUseCase calcularTempoMedioPorFaseUseCase,
            AprovarOrdemServicoUseCase aprovarOrdemServicoUseCase,
            RecusarOrdemServicoUseCase recusarOrdemServicoUseCase) {
        this.criarOrdemServicoUseCase = criarOrdemServicoUseCase;
        this.consultarOrdemServicoUseCase = consultarOrdemServicoUseCase;
        this.consultarOrdemClienteUseCase = consultarOrdemClienteUseCase;
        this.atualizarStatusOrdemUseCase = atualizarStatusOrdemUseCase;
        this.atualizarDiagnosticoConlcuidoUseCase = atualizarDiagnosticoConlcuidoUseCase;
        this.buscarTemposPorOrdemServicoUseCase = buscarTemposPorOrdemServicoUseCase;
        this.calcularTempoMedioPorFaseUseCase = calcularTempoMedioPorFaseUseCase;
        this.aprovarOrdemServicoUseCase = aprovarOrdemServicoUseCase;
        this.recusarOrdemServicoUseCase = recusarOrdemServicoUseCase;
    }

    @PostMapping
    public ResponseEntity<OrdemServicoResponseDTO> CriarOrdemServico(@RequestBody OrdemServicoRequestDTO requestDTO) {
        OrdemServicoResponseDTO response = criarOrdemServicoUseCase.executar(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdemServicoResponseDTO> consultarOrdemServico(@PathVariable Long id) {
        return ResponseEntity.ok(consultarOrdemServicoUseCase.executar(id));
    }

    @GetMapping("/cliente/{documentoCliente}")
    public ResponseEntity<List<OrdemServicoResponseDTO>> consultarPorCliente(@PathVariable String documentoCliente) {
        return ResponseEntity.ok(consultarOrdemClienteUseCase.executar(documentoCliente));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrdemServicoResponseDTO> atualizarStatus(@PathVariable Long id,
            @RequestBody AtualizarStatusOrdemServicoRequestDTO requestDTO) {
        return ResponseEntity.ok(atualizarStatusOrdemUseCase.executar(id, requestDTO.getStatus()));
    }

    @PatchMapping("/{id}/diagnostico-concluido")
    public ResponseEntity<OrdemServicoResponseDTO> concluirDiagnostico(@PathVariable Long id,
            @RequestBody DiagnosticoConcluidoRequestDTO requestDTO) {
        return ResponseEntity.ok(atualizarDiagnosticoConlcuidoUseCase.executar(id, requestDTO));
    }

    @GetMapping("/{id}/tempos")
    public ResponseEntity<List<TempoPorFaseResponseDTO>> buscarTemposPorOrdemServico(@PathVariable Long id) {
        return ResponseEntity.ok(buscarTemposPorOrdemServicoUseCase.executar(id));
    }

    @GetMapping("/metricas/tempo-medio-por-fase")
    public ResponseEntity<List<TempoMedioPorFaseResponseDTO>> tempoMedioPorFase() {
        return ResponseEntity.ok(calcularTempoMedioPorFaseUseCase.executar());
    }

    @PostMapping("/{id}/aprovar")
    public ResponseEntity<OrdemServicoResponseDTO> aprovarOrdemServico(@PathVariable Long id) {
        return ResponseEntity.ok(aprovarOrdemServicoUseCase.executar(id));
    }

    @GetMapping("/{id}/aprovar")
    public ResponseEntity<?> aprovarViaLink(
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
    public ResponseEntity<OrdemServicoResponseDTO> recusarOrdemServico(@PathVariable Long id) {
        return ResponseEntity.ok(recusarOrdemServicoUseCase.executar(id));
    }

    @GetMapping("/{id}/recusar")
    public ResponseEntity<?> recusarViaLink(
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