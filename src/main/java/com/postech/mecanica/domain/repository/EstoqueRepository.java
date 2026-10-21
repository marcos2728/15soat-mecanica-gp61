package com.postech.mecanica.domain.repository;

import java.util.List;
import java.util.Optional;

import com.postech.mecanica.domain.entity.Estoque;

public interface EstoqueRepository {

    Estoque salvar(Estoque estoque);

    Optional<Estoque> consultarPorId(Long id);

    List<Estoque> consultarTodos();

    void deletarPorId(Long id);

    boolean existsById(Long id);

}
