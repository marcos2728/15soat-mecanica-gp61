package com.postech.mecanica.application.useCase.estoque;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.postech.mecanica.application.dto.EstoqueRequestDTO;
import com.postech.mecanica.application.dto.EstoqueResponseDTO;
import com.postech.mecanica.domain.entity.Estoque;
import com.postech.mecanica.domain.event.EstoqueReabastecidoEvent;
import com.postech.mecanica.domain.repository.EstoqueRepository;
import com.postech.mecanica.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class AtualizarItemEstoqueUseCaseTest {

    @Mock
    private EstoqueRepository estoqueRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private AtualizarItemEstoqueUseCase atualizarItemEstoqueUseCase;

    @Test
    @DisplayName("Deve atualizar item de estoque, publicar evento e retornar DTO consolidado")
    void deveAtualizarItemEstoqueComSucesso() {
        Long id = 1L;
        Estoque estoque = new Estoque(id, "Pneu", 5, 300.0);
        EstoqueRequestDTO request = new EstoqueRequestDTO("Pneu Aro 15", 15, 320.0);

        when(estoqueRepository.consultarPorId(id))
                .thenReturn(Optional.of(estoque))
                .thenReturn(Optional.of(estoque));

        doAnswer(invocation -> {
            EstoqueReabastecidoEvent evento = invocation.getArgument(0);
            evento.registrarOrdemLiberada(100L);
            return null;
        }).when(eventPublisher).publishEvent(any(EstoqueReabastecidoEvent.class));

        EstoqueResponseDTO resultado = atualizarItemEstoqueUseCase.executar(id, request);

        assertNotNull(resultado);
        assertEquals("Pneu Aro 15", resultado.nomeItem());
        assertEquals(15, resultado.quantidadeItem());
        assertEquals(320.0, resultado.valorItem());
        assertEquals(1, resultado.ordensLiberadas().size());
        assertEquals(100L, resultado.ordensLiberadas().get(0));

        verify(estoqueRepository).salvar(estoque);
        verify(eventPublisher).publishEvent(any(EstoqueReabastecidoEvent.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando tentar atualizar item inexistente")
    void deveLancarExcecaoQuandoItemNaoEncontrado() {
        Long id = 99L;
        EstoqueRequestDTO request = new EstoqueRequestDTO("Pneu", 10, 300.0);
        when(estoqueRepository.consultarPorId(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> atualizarItemEstoqueUseCase.executar(id, request));
        verify(eventPublisher, never()).publishEvent(any());
    }
}
