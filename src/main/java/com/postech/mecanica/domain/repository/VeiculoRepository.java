package com.postech.mecanica.domain.repository;

import java.util.List;
import java.util.Optional;

import com.postech.mecanica.domain.entity.Veiculo;

public interface VeiculoRepository {

    Veiculo salvar(Veiculo veiculo);

    Optional<Veiculo> consultarPorId(Long id);

    List<Veiculo> buscarTodos();

    void deletarPorId(Long id);

    boolean existsById(Long id);

}
