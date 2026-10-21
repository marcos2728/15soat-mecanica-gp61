package com.postech.mecanica.domain.repository;

import java.util.List;
import java.util.Optional;

import com.postech.mecanica.domain.entity.Estoque;

public interface EstoqueRepository {

    Estoque salvar(Estoque estoque);

    Optional<Estoque> consultarPorId(Long id);

    List<Estoque> buscarTodos();

    void deletarPorId(Long id);

    boolean existsById(Long id);

    default Optional<Estoque> findById(Long id) {
        return consultarPorId(id);
    }

    default Estoque save(Estoque estoque) {
        return salvar(estoque);
    }

    default List<Estoque> findAll() {
        return buscarTodos();
    }

    default void deleteById(Long id) {
        deletarPorId(id);
    }
}
