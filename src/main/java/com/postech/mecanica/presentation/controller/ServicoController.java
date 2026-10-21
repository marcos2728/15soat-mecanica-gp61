package com.postech.mecanica.presentation.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.postech.mecanica.application.dto.ServicoDTO;
import com.postech.mecanica.application.useCase.ServicoService;

@RestController
@RequestMapping("/servicos")
public class ServicoController {

    private final ServicoService ServicoService;

    public ServicoController(ServicoService ServicoService) {
        this.ServicoService = ServicoService;
    }

    @PostMapping
    public ServicoDTO criar(@RequestBody ServicoDTO ServicoDTO) {
        return ServicoService.salvar(ServicoDTO);
    }

    @GetMapping
    public List<ServicoDTO> listar() {
        return ServicoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ServicoDTO obterPorId(@PathVariable Long id) {
        return ServicoService.ServicoPorId(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> atualizar(@PathVariable Long id, @RequestBody ServicoDTO ServicoDTO) {
        ServicoService.atualizar(id, ServicoDTO);
        return ResponseEntity.ok("Veículo atualizado com sucesso");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        ServicoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

}
