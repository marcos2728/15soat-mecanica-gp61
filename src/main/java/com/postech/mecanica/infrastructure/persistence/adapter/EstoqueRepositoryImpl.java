package com.postech.mecanica.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.postech.mecanica.domain.entity.Estoque;
import com.postech.mecanica.domain.repository.EstoqueRepository;
import com.postech.mecanica.infrastructure.persistence.mapper.EstoquePersistenceMapper;
import com.postech.mecanica.infrastructure.persistence.repository.EstoqueRepositoryJpa;

@Repository
public class EstoqueRepositoryImpl implements EstoqueRepository {

    private final EstoqueRepositoryJpa estoqueRepositoryJpa;
    private final EstoquePersistenceMapper estoquePersistenceMapper;

    public EstoqueRepositoryImpl(EstoqueRepositoryJpa estoqueRepositoryJpa,
            EstoquePersistenceMapper estoquePersistenceMapper) {
        this.estoqueRepositoryJpa = estoqueRepositoryJpa;
        this.estoquePersistenceMapper = estoquePersistenceMapper;
    }

    @Override
    public Estoque salvar(Estoque estoque) {
        var entity = estoquePersistenceMapper.toEntity(estoque);
        var salva = estoqueRepositoryJpa.save(entity);
        return estoquePersistenceMapper.toDomain(salva);
    }

    @Override
    public Optional<Estoque> consultarPorId(Long id) {
        return estoqueRepositoryJpa.findById(id).map(estoquePersistenceMapper::toDomain);
    }

    @Override
    public List<Estoque> consultarTodos() {
        return estoqueRepositoryJpa.findAll().stream()
                .map(estoquePersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public void deletarPorId(Long id) {
        estoqueRepositoryJpa.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return estoqueRepositoryJpa.existsById(id);
    }
}
