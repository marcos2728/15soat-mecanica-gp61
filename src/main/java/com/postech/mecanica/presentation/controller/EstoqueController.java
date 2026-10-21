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

import com.postech.mecanica.application.dto.EstoqueRequestDTO;
import com.postech.mecanica.application.dto.EstoqueResponseDTO;
import com.postech.mecanica.application.useCase.estoque.AtualizarItemEstoqueUseCase;
import com.postech.mecanica.application.useCase.estoque.CadastrarItemEstoqueUseCase;
import com.postech.mecanica.application.useCase.estoque.ConsultarItemEstoqueUseCase;
import com.postech.mecanica.application.useCase.estoque.ConsultarTodasItensEstoqueUseCase;
import com.postech.mecanica.application.useCase.estoque.DeletarItemEstoqueUseCase;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/estoque")
public class EstoqueController {

    private final CadastrarItemEstoqueUseCase cadastrarItemEstoqueUseCase;
    private final ConsultarTodasItensEstoqueUseCase consultarTodasItensEstoqueUseCase;
    private final ConsultarItemEstoqueUseCase consultarItemEstoqueUseCase;
    private final AtualizarItemEstoqueUseCase atualizarItemEstoqueUseCase;
    private final DeletarItemEstoqueUseCase deletarItemEstoqueUseCase;

    public EstoqueController(
            CadastrarItemEstoqueUseCase cadastrarItemEstoqueUseCase,
            ConsultarTodasItensEstoqueUseCase consultarTodasItensEstoqueUseCase,
            ConsultarItemEstoqueUseCase consultarItemEstoqueUseCase,
            AtualizarItemEstoqueUseCase atualizarItemEstoqueUseCase,
            DeletarItemEstoqueUseCase deletarItemEstoqueUseCase) {
        this.cadastrarItemEstoqueUseCase = cadastrarItemEstoqueUseCase;
        this.consultarTodasItensEstoqueUseCase = consultarTodasItensEstoqueUseCase;
        this.consultarItemEstoqueUseCase = consultarItemEstoqueUseCase;
        this.atualizarItemEstoqueUseCase = atualizarItemEstoqueUseCase;
        this.deletarItemEstoqueUseCase = deletarItemEstoqueUseCase;
    }

    @PostMapping
    public ResponseEntity<EstoqueResponseDTO> salvar(@Valid @RequestBody EstoqueRequestDTO estoqueDTO) {
        EstoqueResponseDTO salvo = cadastrarItemEstoqueUseCase.executar(estoqueDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping
    public ResponseEntity<List<EstoqueResponseDTO>> listarTodos() {
        return ResponseEntity.ok(consultarTodasItensEstoqueUseCase.executar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstoqueResponseDTO> consultarItemId(@PathVariable Long id) {
        return ResponseEntity.ok(consultarItemEstoqueUseCase.executar(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstoqueResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody EstoqueRequestDTO estoqueDTO) {
        EstoqueResponseDTO atualizado = atualizarItemEstoqueUseCase.executar(id, estoqueDTO);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        deletarItemEstoqueUseCase.executar(id);
        return ResponseEntity.noContent().build();
    }
}