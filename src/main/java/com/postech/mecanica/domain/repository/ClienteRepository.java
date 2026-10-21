package com.postech.mecanica.domain.repository;

import java.util.List;
import java.util.Optional;
import com.postech.mecanica.domain.entity.Cliente;

public interface ClienteRepository {

    Cliente salvar(Cliente cliente);

    Optional<Cliente> consultarDocumentoCliente(String documentoCliente);

    Optional<Cliente> consultarPorId(Long id);

    Optional<Cliente> consultarPorVeiculoId(Long veiculoId);

    List<Cliente> buscarTodos();

    void deletarPorId(Long id);

    boolean existsById(Long id);

}
