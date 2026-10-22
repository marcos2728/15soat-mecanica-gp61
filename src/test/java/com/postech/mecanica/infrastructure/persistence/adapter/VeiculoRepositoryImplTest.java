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

import com.postech.mecanica.domain.entity.Veiculo;
import com.postech.mecanica.infrastructure.persistence.entity.VeiculoEntity;
import com.postech.mecanica.infrastructure.persistence.mapper.VeiculoPersistenceMapper;
import com.postech.mecanica.infrastructure.persistence.repository.VeiculoRepositoryJpa;

@ExtendWith(MockitoExtension.class)
class VeiculoRepositoryImplTest {

    @Mock
    private VeiculoRepositoryJpa veiculoRepositoryJpa;

    @Mock
    private VeiculoPersistenceMapper mapper;

    @InjectMocks
    private VeiculoRepositoryImpl repository;

    @Test
    @DisplayName("Deve salvar veículo com sucesso")
    void deveSalvarVeiculo() {
        Veiculo domain = mock(Veiculo.class);
        VeiculoEntity entity = mock(VeiculoEntity.class);

        when(mapper.toEntity(domain)).thenReturn(entity);
        when(veiculoRepositoryJpa.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(domain);

        Veiculo resultado = repository.salvar(domain);

        assertNotNull(resultado);
        verify(veiculoRepositoryJpa).save(entity);
    }

    @Test
    @DisplayName("Deve consultar veículo por ID")
    void deveConsultarPorId() {
        Veiculo domain = mock(Veiculo.class);
        VeiculoEntity entity = mock(VeiculoEntity.class);

        when(veiculoRepositoryJpa.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        Optional<Veiculo> resultado = repository.consultarPorId(1L);

        assertTrue(resultado.isPresent());
    }

    @Test
    @DisplayName("Deve buscar todos os veículos")
    void deveBuscarTodos() {
        Veiculo domain = mock(Veiculo.class);
        VeiculoEntity entity = mock(VeiculoEntity.class);

        when(veiculoRepositoryJpa.findAll()).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        List<Veiculo> resultado = repository.buscarTodos();

        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("Deve deletar veículo por ID e verificar existsById")
    void deveDeletarEVerificarExists() {
        assertDoesNotThrow(() -> repository.deletarPorId(1L));
        verify(veiculoRepositoryJpa).deleteById(1L);

        when(veiculoRepositoryJpa.existsById(1L)).thenReturn(true);
        assertTrue(repository.existsById(1L));
    }
}
