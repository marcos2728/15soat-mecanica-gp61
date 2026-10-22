package com.postech.mecanica.infrastructure.persistence.adapter;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.postech.mecanica.domain.entity.Cliente;
import com.postech.mecanica.infrastructure.persistence.entity.ClienteEntity;
import com.postech.mecanica.infrastructure.persistence.mapper.ClientePersistenceMapper;
import com.postech.mecanica.infrastructure.persistence.repository.ClienteRepositoryJpa;

@ExtendWith(MockitoExtension.class)
class ClienteRepositoryImplTest {

    @Mock
    private ClienteRepositoryJpa clienteRepositoryJpa;

    @Mock
    private ClientePersistenceMapper mapper;

    @InjectMocks
    private ClienteRepositoryImpl repository;

    @Test
    @DisplayName("Deve salvar cliente com sucesso")
    void deveSalvarCliente() {
        Cliente domain = mock(Cliente.class);
        ClienteEntity entity = mock(ClienteEntity.class);

        when(mapper.toEntity(domain)).thenReturn(entity);
        when(clienteRepositoryJpa.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(domain);

        Cliente resultado = repository.salvar(domain);

        assertNotNull(resultado);
        verify(clienteRepositoryJpa).save(entity);
    }

    @Test
    @DisplayName("Deve consultar cliente por documento")
    void deveConsultarPorDocumento() {
        Cliente domain = mock(Cliente.class);
        ClienteEntity entity = mock(ClienteEntity.class);

        when(clienteRepositoryJpa.findByCpfCnpjComVeiculo("12345678901")).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        Optional<Cliente> resultado = repository.consultarDocumentoCliente("12345678901");

        assertTrue(resultado.isPresent());
    }

    @Test
    @DisplayName("Deve consultar cliente por id")
    void deveConsultarPorId() {
        Cliente domain = mock(Cliente.class);
        ClienteEntity entity = mock(ClienteEntity.class);

        when(clienteRepositoryJpa.findByIdComVeiculo(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        Optional<Cliente> resultado = repository.consultarPorId(1L);

        assertTrue(resultado.isPresent());
    }

    @Test
    @DisplayName("Deve consultar cliente por id do veiculo")
    void deveConsultarPorVeiculoId() {
        Cliente domain = mock(Cliente.class);
        ClienteEntity entity = mock(ClienteEntity.class);

        when(clienteRepositoryJpa.findByVeiculoId(10L)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        Optional<Cliente> resultado = repository.consultarPorVeiculoId(10L);

        assertTrue(resultado.isPresent());
    }

    @Test
    @DisplayName("Deve buscar todos os clientes")
    void deveBuscarTodos() {
        Cliente domain = mock(Cliente.class);
        ClienteEntity entity = mock(ClienteEntity.class);

        when(clienteRepositoryJpa.findAllComVeiculo()).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        List<Cliente> resultado = repository.buscarTodos();

        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("Deve deletar cliente por id e verificar existsById")
    void deveDeletarEVerificarExists() {
        assertDoesNotThrow(() -> repository.deletarPorId(1L));
        verify(clienteRepositoryJpa).deleteById(1L);

        when(clienteRepositoryJpa.existsById(1L)).thenReturn(true);
        assertTrue(repository.existsById(1L));
    }
}
