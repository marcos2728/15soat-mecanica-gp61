package com.postech.mecanica.presentation.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.postech.mecanica.application.dto.ServicoRequestDTO;
import com.postech.mecanica.application.dto.ServicoResponseDTO;
import com.postech.mecanica.application.useCase.servico.AtualizarServicoUseCase;
import com.postech.mecanica.application.useCase.servico.CadastrarServicoUseCase;
import com.postech.mecanica.application.useCase.servico.ConsultarServicoUseCase;
import com.postech.mecanica.application.useCase.servico.ConsultarTodosServicosUseCase;
import com.postech.mecanica.application.useCase.servico.DeletarServicoUseCase;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/servicos")
public class ServicoController {

    private final CadastrarServicoUseCase cadastrarServicoUseCase;
    private final ConsultarTodosServicosUseCase consultarTodosServicosUseCase;
    private final ConsultarServicoUseCase consultarServicoUseCase;
    private final AtualizarServicoUseCase atualizarServicoUseCase;
    private final DeletarServicoUseCase deletarServicoUseCase;

    public ServicoController(
            CadastrarServicoUseCase cadastrarServicoUseCase,
            ConsultarTodosServicosUseCase consultarTodosServicosUseCase,
            ConsultarServicoUseCase consultarServicoUseCase,
            AtualizarServicoUseCase atualizarServicoUseCase,
            DeletarServicoUseCase deletarServicoUseCase) {
        this.cadastrarServicoUseCase = cadastrarServicoUseCase;
        this.consultarTodosServicosUseCase = consultarTodosServicosUseCase;
        this.consultarServicoUseCase = consultarServicoUseCase;
        this.atualizarServicoUseCase = atualizarServicoUseCase;
        this.deletarServicoUseCase = deletarServicoUseCase;
    }

    @PostMapping
    public ResponseEntity<ServicoResponseDTO> criar(@Valid @RequestBody ServicoRequestDTO servicoDTO) {
        ServicoResponseDTO salvo = cadastrarServicoUseCase.executar(servicoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping
    public ResponseEntity<List<ServicoResponseDTO>> listar() {
        return ResponseEntity.ok(consultarTodosServicosUseCase.executar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicoResponseDTO> obterPorId(@PathVariable Long id) {
        return ResponseEntity.ok(consultarServicoUseCase.executar(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServicoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ServicoRequestDTO servicoDTO) {
        ServicoResponseDTO atualizado = atualizarServicoUseCase.executar(id, servicoDTO);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        deletarServicoUseCase.executar(id);
        return ResponseEntity.noContent().build();
    }
}
