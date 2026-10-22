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

import com.postech.mecanica.domain.entity.Servico;
import com.postech.mecanica.infrastructure.persistence.entity.ServicoEntity;
import com.postech.mecanica.infrastructure.persistence.mapper.ServicoPersistenceMapper;
import com.postech.mecanica.infrastructure.persistence.repository.ServicoRepositoryJpa;

@ExtendWith(MockitoExtension.class)
class ServicoRepositoryImplTest {

    @Mock
    private ServicoRepositoryJpa servicoRepositoryJpa;

    @Mock
    private ServicoPersistenceMapper mapper;

    @InjectMocks
    private ServicoRepositoryImpl repository;

    @Test
    @DisplayName("Deve salvar serviço com sucesso")
    void deveSalvarServico() {
        Servico domain = mock(Servico.class);
        ServicoEntity entity = mock(ServicoEntity.class);

        when(mapper.toEntity(domain)).thenReturn(entity);
        when(servicoRepositoryJpa.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(domain);

        Servico resultado = repository.salvar(domain);

        assertNotNull(resultado);
        verify(servicoRepositoryJpa).save(entity);
    }

    @Test
    @DisplayName("Deve consultar serviço por ID")
    void deveConsultarPorId() {
        Servico domain = mock(Servico.class);
        ServicoEntity entity = mock(ServicoEntity.class);

        when(servicoRepositoryJpa.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        Optional<Servico> resultado = repository.consultarPorId(1L);

        assertTrue(resultado.isPresent());
    }

    @Test
    @DisplayName("Deve consultar todos os serviços")
    void deveConsultarTodos() {
        Servico domain = mock(Servico.class);
        ServicoEntity entity = mock(ServicoEntity.class);

        when(servicoRepositoryJpa.findAll()).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        List<Servico> resultado = repository.consultarTodos();

        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("Deve deletar serviço por ID e verificar existsById")
    void deveDeletarEVerificarExists() {
        assertDoesNotThrow(() -> repository.deletarPorId(1L));
        verify(servicoRepositoryJpa).deleteById(1L);

        when(servicoRepositoryJpa.existsById(1L)).thenReturn(true);
        assertTrue(repository.existsById(1L));
    }
}
