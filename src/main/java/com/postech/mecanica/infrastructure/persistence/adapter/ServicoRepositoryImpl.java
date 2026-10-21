package com.postech.mecanica.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.postech.mecanica.domain.entity.Servico;
import com.postech.mecanica.domain.repository.ServicoRepository;
import com.postech.mecanica.infrastructure.persistence.mapper.ServicoPersistenceMapper;
import com.postech.mecanica.infrastructure.persistence.repository.ServicoRepositoryJpa;

@Repository
public class ServicoRepositoryImpl implements ServicoRepository {

    private final ServicoRepositoryJpa servicoRepositoryJpa;
    private final ServicoPersistenceMapper servicoPersistenceMapper;

    public ServicoRepositoryImpl(ServicoRepositoryJpa servicoRepositoryJpa,
            ServicoPersistenceMapper servicoPersistenceMapper) {
        this.servicoRepositoryJpa = servicoRepositoryJpa;
        this.servicoPersistenceMapper = servicoPersistenceMapper;
    }

    @Override
    public Servico salvar(Servico servico) {
        var entity = servicoPersistenceMapper.toEntity(servico);
        var salva = servicoRepositoryJpa.save(entity);
        return servicoPersistenceMapper.toDomain(salva);
    }

    @Override
    public Optional<Servico> consultarPorId(Long id) {
        return servicoRepositoryJpa.findById(id).map(servicoPersistenceMapper::toDomain);
    }

    @Override
    public List<Servico> consultarTodos() {
        return servicoRepositoryJpa.findAll().stream()
                .map(servicoPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public void deletarPorId(Long id) {
        servicoRepositoryJpa.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return servicoRepositoryJpa.existsById(id);
    }
}
