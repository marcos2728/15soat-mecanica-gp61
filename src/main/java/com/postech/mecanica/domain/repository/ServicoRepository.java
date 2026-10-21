package com.postech.mecanica.domain.repository;

import java.util.List;
import java.util.Optional;

import com.postech.mecanica.domain.entity.Servico;

public interface ServicoRepository {

    Servico salvar(Servico servico);

    Optional<Servico> consultarPorId(Long id);

    List<Servico> consultarTodos();

    void deletarPorId(Long id);

    boolean existsById(Long id);

}
