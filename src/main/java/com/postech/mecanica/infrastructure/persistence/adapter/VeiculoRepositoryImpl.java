package com.postech.mecanica.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.postech.mecanica.domain.entity.Veiculo;
import com.postech.mecanica.domain.repository.VeiculoRepository;
import com.postech.mecanica.infrastructure.persistence.mapper.VeiculoPersistenceMapper;
import com.postech.mecanica.infrastructure.persistence.repository.VeiculoRepositoryJpa;

@Repository
public class VeiculoRepositoryImpl implements VeiculoRepository {

    private final VeiculoRepositoryJpa veiculoRepositoryJpa;
    private final VeiculoPersistenceMapper veiculoPersistenceMapper;

    public VeiculoRepositoryImpl(VeiculoRepositoryJpa veiculoRepositoryJpa,
            VeiculoPersistenceMapper veiculoPersistenceMapper) {
        this.veiculoRepositoryJpa = veiculoRepositoryJpa;
        this.veiculoPersistenceMapper = veiculoPersistenceMapper;
    }

    @Override
    public Veiculo salvar(Veiculo veiculo) {
        var veiculoEntity = veiculoPersistenceMapper.toEntity(veiculo);
        var veiculoSalvo = veiculoRepositoryJpa.save(veiculoEntity);
        return veiculoPersistenceMapper.toDomain(veiculoSalvo);
    }

    @Override
    public Optional<Veiculo> consultarPorId(Long id) {
        return veiculoRepositoryJpa.findById(id).map(veiculoPersistenceMapper::toDomain);
    }

    @Override
    public List<Veiculo> buscarTodos() {
        return veiculoRepositoryJpa.findAll().stream()
                .map(veiculoPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public void deletarPorId(Long id) {
        veiculoRepositoryJpa.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return veiculoRepositoryJpa.existsById(id);
    }
}
