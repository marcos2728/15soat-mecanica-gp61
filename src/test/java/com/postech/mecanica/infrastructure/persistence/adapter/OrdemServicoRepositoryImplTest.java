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

import com.postech.mecanica.domain.entity.OrdemServico;
import com.postech.mecanica.infrastructure.persistence.entity.OrdemServicoEntity;
import com.postech.mecanica.infrastructure.persistence.mapper.OrdemServicoPersistenceMapper;
import com.postech.mecanica.infrastructure.persistence.repository.OrdemServicoRepositoryJpa;

@ExtendWith(MockitoExtension.class)
class OrdemServicoRepositoryImplTest {

    @Mock
    private OrdemServicoRepositoryJpa ordemServicoRepositoryJpa;

    @Mock
    private OrdemServicoPersistenceMapper mapper;

    @InjectMocks
    private OrdemServicoRepositoryImpl repository;

    @Test
    @DisplayName("Deve salvar ordem de serviço com sucesso")
    void deveSalvarOrdemServico() {
        OrdemServico domain = mock(OrdemServico.class);
        OrdemServicoEntity entity = mock(OrdemServicoEntity.class);

        when(mapper.toEntity(domain)).thenReturn(entity);
        when(ordemServicoRepositoryJpa.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(domain);

        OrdemServico resultado = repository.salvar(domain);

        assertNotNull(resultado);
        verify(ordemServicoRepositoryJpa).save(entity);
    }

    @Test
    @DisplayName("Deve consultar OS por ID")
    void deveConsultarPorId() {
        OrdemServico domain = mock(OrdemServico.class);
        OrdemServicoEntity entity = mock(OrdemServicoEntity.class);

        when(ordemServicoRepositoryJpa.findByIdComVeiculo(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        Optional<OrdemServico> resultado = repository.consultarPorId(1L);

        assertTrue(resultado.isPresent());
    }

    @Test
    @DisplayName("Deve buscar todas as ordens de serviço")
    void deveBuscarTodos() {
        OrdemServico domain = mock(OrdemServico.class);
        OrdemServicoEntity entity = mock(OrdemServicoEntity.class);

        when(ordemServicoRepositoryJpa.findAllComVeiculo()).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        List<OrdemServico> resultado = repository.buscarTodos();

        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("Deve buscar ordens por ID do veículo")
    void deveBuscarPorVeiculoId() {
        OrdemServico domain = mock(OrdemServico.class);
        OrdemServicoEntity entity = mock(OrdemServicoEntity.class);

        when(ordemServicoRepositoryJpa.findByVeiculoId(10L)).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        List<OrdemServico> resultado = repository.buscarPorVeiculoId(10L);

        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("Deve buscar ordens pendentes por ID do estoque")
    void deveBuscarPendentesPorEstoqueId() {
        OrdemServico domain = mock(OrdemServico.class);
        OrdemServicoEntity entity = mock(OrdemServicoEntity.class);

        when(ordemServicoRepositoryJpa.findPendentesPorEstoqueId(5L)).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        List<OrdemServico> resultado = repository.buscarPendentesPorEstoqueId(5L);

        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("Deve deletar OS por ID e verificar existsById")
    void deveDeletarEVerificarExists() {
        assertDoesNotThrow(() -> repository.deletarPorId(1L));
        verify(ordemServicoRepositoryJpa).deleteById(1L);

        when(ordemServicoRepositoryJpa.existsById(1L)).thenReturn(true);
        assertTrue(repository.existsById(1L));
    }
}
