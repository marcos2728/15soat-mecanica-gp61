package com.postech.mecanica.application.useCase.ordemservico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.postech.mecanica.domain.entity.Estoque;
import com.postech.mecanica.domain.entity.OrdemServico;
import com.postech.mecanica.domain.entity.OrdemServicoItemEstoque;
import com.postech.mecanica.domain.entity.Veiculo;
import com.postech.mecanica.domain.enums.StatusOrdemServico;
import com.postech.mecanica.domain.event.EstoqueReabastecidoEvent;
import com.postech.mecanica.domain.repository.EstoqueRepository;
import com.postech.mecanica.domain.repository.OrdemServicoRepository;
import com.postech.mecanica.domain.vo.Placa;

@ExtendWith(MockitoExtension.class)
class ReconciliacaoEstoqueListenerTest {

    @Mock
    private OrdemServicoRepository ordemServicoRepository;

    @Mock
    private EstoqueRepository estoqueRepository;

    @InjectMocks
    private ReconciliacaoEstoqueListener listener;

    @Test
    @DisplayName("Deve ignorar quando evento ou estoqueId for nulo")
    void deveIgnorarEventoNuloOuIdNulo() {
        listener.aoReabastecerEstoque(null);
        listener.aoReabastecerEstoque(new EstoqueReabastecidoEvent(null));

        verify(estoqueRepository, never()).consultarPorId(any());
    }

    @Test
    @DisplayName("Deve ignorar quando estoque não for encontrado no banco")
    void deveIgnorarQuandoEstoqueNaoEncontrado() {
        when(estoqueRepository.consultarPorId(1L)).thenReturn(Optional.empty());

        listener.aoReabastecerEstoque(new EstoqueReabastecidoEvent(1L));

        verify(ordemServicoRepository, never()).buscarPendentesPorEstoqueId(any());
    }

    @Test
    @DisplayName("Deve reconciliar estoque e transitar OS para DIAGNOSTICO_CONCLUIDO quando todas as peças ficarem disponíveis")
    void deveReconciliarEstoqueComSucesso() {
        Long estoqueId = 5L;
        Estoque estoque = new Estoque(estoqueId, "Vela de ignição", 4, 30.0);
        when(estoqueRepository.consultarPorId(estoqueId)).thenReturn(Optional.of(estoque));

        Veiculo veiculo = new Veiculo(1L, new Placa("ABC1D23"), "Corsa", "Chevrolet", 2015);
        OrdemServicoItemEstoque itemEstoque = new OrdemServicoItemEstoque(estoqueId, "Vela de ignição", 4, 30.0, false);
        List<OrdemServicoItemEstoque> pecas = new ArrayList<>(List.of(itemEstoque));

        OrdemServico os = new OrdemServico(10L, veiculo, StatusOrdemServico.PENDENTE_ESTOQUE, "Troca de velas", pecas, new ArrayList<>(), 0.0, LocalDateTime.now(), null);

        when(ordemServicoRepository.buscarPendentesPorEstoqueId(estoqueId)).thenReturn(List.of(os));

        EstoqueReabastecidoEvent evento = new EstoqueReabastecidoEvent(estoqueId);
        listener.aoReabastecerEstoque(evento);

        assertTrue(itemEstoque.getDisponivel());
        assertEquals(0, estoque.getQuantidadeItem()); // baixou as 4
        assertEquals(StatusOrdemServico.DIAGNOSTICO_CONCLUIDO, os.getStatus());
        assertEquals(List.of(10L), evento.getOrdensServicoLiberadas());

        verify(ordemServicoRepository).salvar(os);
        verify(estoqueRepository).salvar(estoque);
    }

    @Test
    @DisplayName("Não deve atualizar OS se estoque for insuficiente")
    void naoDeveAtualizarSeEstoqueInsuficiente() {
        Long estoqueId = 5L;
        Estoque estoque = new Estoque(estoqueId, "Vela", 2, 30.0); // insuficiente para 4
        when(estoqueRepository.consultarPorId(estoqueId)).thenReturn(Optional.of(estoque));

        Veiculo veiculo = new Veiculo(1L, new Placa("ABC1D23"), "Corsa", "Chevrolet", 2015);
        OrdemServicoItemEstoque itemEstoque = new OrdemServicoItemEstoque(estoqueId, "Vela", 4, 30.0, false);
        List<OrdemServicoItemEstoque> pecas = new ArrayList<>(List.of(itemEstoque));

        OrdemServico os = new OrdemServico(10L, veiculo, StatusOrdemServico.PENDENTE_ESTOQUE, "Troca de velas", pecas, new ArrayList<>(), 0.0, LocalDateTime.now(), null);

        when(ordemServicoRepository.buscarPendentesPorEstoqueId(estoqueId)).thenReturn(List.of(os));

        EstoqueReabastecidoEvent evento = new EstoqueReabastecidoEvent(estoqueId);
        listener.aoReabastecerEstoque(evento);

        assertFalse(itemEstoque.getDisponivel());
        assertEquals(2, estoque.getQuantidadeItem()); // não baixou
        assertEquals(StatusOrdemServico.PENDENTE_ESTOQUE, os.getStatus());
        assertTrue(evento.getOrdensServicoLiberadas().isEmpty());

        verify(ordemServicoRepository, never()).salvar(os);
        verify(estoqueRepository).salvar(estoque);
    }

    @Test
    @DisplayName("Deve ignorar OSs que não estejam no status PENDENTE_ESTOQUE")
    void deveIgnorarOsComOutroStatus() {
        Long estoqueId = 5L;
        Estoque estoque = new Estoque(estoqueId, "Vela", 10, 30.0);
        when(estoqueRepository.consultarPorId(estoqueId)).thenReturn(Optional.of(estoque));

        Veiculo veiculo = new Veiculo(1L, new Placa("ABC1D23"), "Corsa", "Chevrolet", 2015);
        OrdemServico os = new OrdemServico(10L, veiculo, StatusOrdemServico.APROVADA, "Outra OS", new ArrayList<>(), new ArrayList<>(), 0.0, LocalDateTime.now(), null);

        when(ordemServicoRepository.buscarPendentesPorEstoqueId(estoqueId)).thenReturn(List.of(os));

        EstoqueReabastecidoEvent evento = new EstoqueReabastecidoEvent(estoqueId);
        listener.aoReabastecerEstoque(evento);

        verify(ordemServicoRepository, never()).salvar(os);
    }
}
