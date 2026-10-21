package com.postech.mecanica.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.postech.mecanica.domain.entity.Cliente;
import com.postech.mecanica.domain.repository.ClienteRepository;
import com.postech.mecanica.infrastructure.persistence.mapper.ClientePersistenceMapper;
import com.postech.mecanica.infrastructure.persistence.repository.ClienteRepositoryJpa;

@Repository
public class ClienteRepositoryImpl implements ClienteRepository {

    private final ClienteRepositoryJpa clienteRepositoryJpa;
    private final ClientePersistenceMapper mapper;

    public ClienteRepositoryImpl(ClienteRepositoryJpa clienteRepositoryJpa, ClientePersistenceMapper mapper) {
        this.clienteRepositoryJpa = clienteRepositoryJpa;
        this.mapper = mapper;
    }

    @Override
    public Cliente salvar(Cliente cliente) {
        var entity = mapper.toEntity(cliente);
        var salva = clienteRepositoryJpa.save(entity);
        return mapper.toDomain(salva);
    }

    @Override
    public Optional<Cliente> consultarDocumentoCliente(String cpfCnpj) {
        return clienteRepositoryJpa.findByCpfCnpjComVeiculo(cpfCnpj).map(mapper::toDomain);
    }

    @Override
    public Optional<Cliente> consultarPorId(Long id) {
        return clienteRepositoryJpa.findByIdComVeiculo(id).map(mapper::toDomain);
    }

    @Override
    public List<Cliente> buscarTodos() {
        return clienteRepositoryJpa.findAllComVeiculo().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deletarPorId(Long id) {
        clienteRepositoryJpa.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return clienteRepositoryJpa.existsById(id);
    }

}
