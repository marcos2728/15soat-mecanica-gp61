package com.postech.mecanica.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.postech.mecanica.domain.entity.OrdemServico;
import com.postech.mecanica.domain.repository.OrdemServicoRepository;
import com.postech.mecanica.infrastructure.persistence.mapper.OrdemServicoPersistenceMapper;
import com.postech.mecanica.infrastructure.persistence.repository.OrdemServicoRepositoryJpa;

@Repository
public class OrdemServicoRepositoryImpl implements OrdemServicoRepository {

    private final OrdemServicoRepositoryJpa ordemServicoRepositoryJpa;
    private final OrdemServicoPersistenceMapper mapper;

    public OrdemServicoRepositoryImpl(OrdemServicoRepositoryJpa ordemServicoRepositoryJpa,
            OrdemServicoPersistenceMapper mapper) {
        this.ordemServicoRepositoryJpa = ordemServicoRepositoryJpa;
        this.mapper = mapper;
    }

    @Override
    public OrdemServico salvar(OrdemServico ordemServico) {
        var entity = mapper.toEntity(ordemServico);
        var salva = ordemServicoRepositoryJpa.save(entity);
        return mapper.toDomain(salva);
    }

    @Override
    public Optional<OrdemServico> consultarPorId(Long id) {
        return ordemServicoRepositoryJpa.findByIdComVeiculo(id).map(mapper::toDomain);
    }

    @Override
    public List<OrdemServico> buscarTodos() {
        return ordemServicoRepositoryJpa.findAllComVeiculo().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<OrdemServico> buscarPorVeiculoId(Long veiculoId) {
        return ordemServicoRepositoryJpa.findByVeiculoId(veiculoId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<OrdemServico> buscarPendentesPorEstoqueId(Long estoqueId) {
        return ordemServicoRepositoryJpa.findPendentesPorEstoqueId(estoqueId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deletarPorId(Long id) {
        ordemServicoRepositoryJpa.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return ordemServicoRepositoryJpa.existsById(id);
    }

}
