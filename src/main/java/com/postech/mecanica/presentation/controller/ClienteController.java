package com.postech.mecanica.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.postech.mecanica.application.dto.ClienteRequestDTO;
import com.postech.mecanica.application.dto.ClienteVeiculoResponseDTO;
import com.postech.mecanica.application.dto.ClienteVeiculoRequestDTO;
import com.postech.mecanica.application.useCase.cliente.AtualizarClienteUseCase;
import com.postech.mecanica.application.useCase.cliente.CadastrarClienteUseCase;
import com.postech.mecanica.application.useCase.cliente.ConsultarClienteUseCase;
import com.postech.mecanica.application.useCase.cliente.DeletarClienteUseCase;

import jakarta.validation.Valid;

/**
 * Todos os clientes devem ter um veículo associado.
 */
@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final CadastrarClienteUseCase cadastrarClienteUseCase;
    private final ConsultarClienteUseCase consultarClienteUseCase;
    private final AtualizarClienteUseCase atualizarClienteUseCase;
    private final DeletarClienteUseCase deletarClienteUseCase;

    public ClienteController(CadastrarClienteUseCase cadastrarClienteUseCase,
            ConsultarClienteUseCase consultarClienteUseCase, AtualizarClienteUseCase atualizarClienteUseCase,
            DeletarClienteUseCase deletarClienteUseCase) {
        this.cadastrarClienteUseCase = cadastrarClienteUseCase;
        this.consultarClienteUseCase = consultarClienteUseCase;
        this.atualizarClienteUseCase = atualizarClienteUseCase;
        this.deletarClienteUseCase = deletarClienteUseCase;
    }

    /**
     * @param clienteDTO dados do cliente e veículo a serem cadastrados
     * @return ResponseEntity com status 201 e dados do cliente salvo
     */
    @PostMapping
    public ResponseEntity<ClienteVeiculoResponseDTO> criar(@Valid @RequestBody ClienteVeiculoRequestDTO clienteDTO) {
        ClienteVeiculoResponseDTO clienteSalvo = cadastrarClienteUseCase.executar(clienteDTO);
        return ResponseEntity.status(201).body(clienteSalvo);
    }

    /**
     * Busca um cliente pelo CPF ou CNPJ.
     *
     * @param cpfCnpj o CPF ou CNPJ do cliente a ser buscado
     * @return ResponseEntity com dados do cliente encontrado
     */
    @GetMapping("/{cpfCnpj}")
    public ResponseEntity<ClienteVeiculoResponseDTO> buscarPorCpfCnpj(@PathVariable String cpfCnpj) {
        return ResponseEntity.ok(consultarClienteUseCase.executar(cpfCnpj));
    }

    /**
     * Atualiza um cliente existente.
     *
     * @param id         identificador único do cliente a ser atualizado
     * @param clienteDTO novos dados do cliente
     * @return ResponseEntity com mensagem de sucesso
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> AtualizarCliente(@PathVariable Long id,
            @Valid @RequestBody ClienteRequestDTO clienteDTO) {
        atualizarClienteUseCase.executar(id, clienteDTO);
        return ResponseEntity.ok("Cliente atualizado com sucesso");
    }

    /**
     * Deleta um cliente.
     *
     * @param id identificador único do cliente a ser deletado
     * @return ResponseEntity com status 204 (No Content)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        deletarClienteUseCase.executar(id);
        return ResponseEntity.noContent().build();
    }
}
