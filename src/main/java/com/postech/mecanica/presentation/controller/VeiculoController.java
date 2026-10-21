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

import com.postech.mecanica.application.dto.VeiculoRequestDTO;
import com.postech.mecanica.application.dto.VeiculoResponseDTO;
import com.postech.mecanica.application.useCase.veiculo.AtualizarVeiculoUseCase;
import com.postech.mecanica.application.useCase.veiculo.CadastrarVeiculoUseCase;
import com.postech.mecanica.application.useCase.veiculo.ConsultarVeiculoUseCase;
import com.postech.mecanica.application.useCase.veiculo.ConsultarVeiculosClienteUseCase;
import com.postech.mecanica.application.useCase.veiculo.DeletarVeiculoUseCase;

@RestController
@RequestMapping("/veiculos")
public class VeiculoController {

    private final CadastrarVeiculoUseCase cadastrarVeiculoUseCase;
    private final AtualizarVeiculoUseCase atualizarVeiculoUsecase;
    private final ConsultarVeiculoUseCase consultarVeiculoUsecase;
    private final ConsultarVeiculosClienteUseCase consultarVeiculosClienteUseCase;
    private final DeletarVeiculoUseCase deletarVeiculoUseCase;

    public VeiculoController(CadastrarVeiculoUseCase cadastrarVeiculoUseCase,
            AtualizarVeiculoUseCase atualizarVeiculoUsecase,
            ConsultarVeiculoUseCase consultarVeiculoUsecase,
            ConsultarVeiculosClienteUseCase consultarVeiculosClienteUseCase,
            DeletarVeiculoUseCase deletarVeiculoUseCase) {
        this.cadastrarVeiculoUseCase = cadastrarVeiculoUseCase;
        this.atualizarVeiculoUsecase = atualizarVeiculoUsecase;
        this.consultarVeiculoUsecase = consultarVeiculoUsecase;
        this.consultarVeiculosClienteUseCase = consultarVeiculosClienteUseCase;
        this.deletarVeiculoUseCase = deletarVeiculoUseCase;
    }

    @PostMapping("/{idCliente}")
    public VeiculoResponseDTO cadastrarVeiculo(@PathVariable Long idCliente,
            @RequestBody VeiculoRequestDTO veiculoDTO) {
        return cadastrarVeiculoUseCase.executar(idCliente, veiculoDTO);
    }

    @GetMapping("/cliente/{documento}")
    public List<VeiculoResponseDTO> consultarVeiculosCliente(@PathVariable String documento) {
        return consultarVeiculosClienteUseCase.executar(documento);
    }

    @GetMapping("/{id}")
    public VeiculoResponseDTO consultarPorId(@PathVariable Long id) {
        return consultarVeiculoUsecase.executar(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> atualizarVeiculo(@PathVariable Long id, @RequestBody VeiculoRequestDTO veiculoDTO) {
        atualizarVeiculoUsecase.executar(id, veiculoDTO);
        return ResponseEntity.ok("Veículo atualizado com sucesso");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        deletarVeiculoUseCase.executar(id);
        return ResponseEntity.noContent().build();
    }
}
