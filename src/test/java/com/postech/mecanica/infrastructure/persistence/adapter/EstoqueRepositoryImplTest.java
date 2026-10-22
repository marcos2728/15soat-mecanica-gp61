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

import com.postech.mecanica.domain.entity.Estoque;
import com.postech.mecanica.infrastructure.persistence.entity.EstoqueEntity;
import com.postech.mecanica.infrastructure.persistence.mapper.EstoquePersistenceMapper;
import com.postech.mecanica.infrastructure.persistence.repository.EstoqueRepositoryJpa;

@ExtendWith(MockitoExtension.class)
class EstoqueRepositoryImplTest {

    @Mock
    private EstoqueRepositoryJpa estoqueRepositoryJpa;

    @Mock
    private EstoquePersistenceMapper mapper;

    @InjectMocks
    private EstoqueRepositoryImpl repository;

    @Test
    @DisplayName("Deve salvar estoque com sucesso")
    void deveSalvarEstoque() {
        Estoque domain = mock(Estoque.class);
        EstoqueEntity entity = mock(EstoqueEntity.class);

        when(mapper.toEntity(domain)).thenReturn(entity);
        when(estoqueRepositoryJpa.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(domain);

        Estoque resultado = repository.salvar(domain);

        assertNotNull(resultado);
        verify(estoqueRepositoryJpa).save(entity);
    }

    @Test
    @DisplayName("Deve consultar estoque por ID")
    void deveConsultarPorId() {
        Estoque domain = mock(Estoque.class);
        EstoqueEntity entity = mock(EstoqueEntity.class);

        when(estoqueRepositoryJpa.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        Optional<Estoque> resultado = repository.consultarPorId(1L);

        assertTrue(resultado.isPresent());
    }

    @Test
    @DisplayName("Deve consultar todos os itens de estoque")
    void deveConsultarTodos() {
        Estoque domain = mock(Estoque.class);
        EstoqueEntity entity = mock(EstoqueEntity.class);

        when(estoqueRepositoryJpa.findAll()).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        List<Estoque> resultado = repository.consultarTodos();

        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("Deve deletar item de estoque por ID e verificar existsById")
    void deveDeletarEVerificarExists() {
        assertDoesNotThrow(() -> repository.deletarPorId(1L));
        verify(estoqueRepositoryJpa).deleteById(1L);

        when(estoqueRepositoryJpa.existsById(1L)).thenReturn(true);
        assertTrue(repository.existsById(1L));
    }
}
