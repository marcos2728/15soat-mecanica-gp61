package com.postech.mecanica.application.useCase.estoque;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.postech.mecanica.domain.entity.OrdemServico;
import com.postech.mecanica.domain.repository.EstoqueRepository;
import com.postech.mecanica.domain.repository.OrdemServicoRepository;
import com.postech.mecanica.exception.RegraNegocioException;
import com.postech.mecanica.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class DeletarItemEstoqueUseCaseTest {

    @Mock
    private EstoqueRepository estoqueRepository;

    @Mock
    private OrdemServicoRepository ordemServicoRepository;

    @InjectMocks
    private DeletarItemEstoqueUseCase deletarItemEstoqueUseCase;

    @Test
    @DisplayName("Deve deletar item de estoque com sucesso quando existir e não houver OSs pendentes")
    void deveDeletarItemComSucesso() {
        Long id = 1L;
        when(estoqueRepository.existsById(id)).thenReturn(true);
        when(ordemServicoRepository.buscarPendentesPorEstoqueId(id)).thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> deletarItemEstoqueUseCase.executar(id));
        verify(estoqueRepository).deletarPorId(id);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando o item de estoque não existir")
    void deveLancarExcecaoQuandoNaoExistir() {
        Long id = 99L;
        when(estoqueRepository.existsById(id)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> deletarItemEstoqueUseCase.executar(id));
        verify(estoqueRepository, never()).deletarPorId(id);
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException quando houver ordens de serviço pendentes do item")
    void deveLancarExcecaoQuandoHouverOsPendentes() {
        Long id = 1L;
        OrdemServico os = org.mockito.Mockito.mock(OrdemServico.class);
        when(estoqueRepository.existsById(id)).thenReturn(true);
        when(ordemServicoRepository.buscarPendentesPorEstoqueId(id)).thenReturn(List.of(os));

        assertThrows(RegraNegocioException.class, () -> deletarItemEstoqueUseCase.executar(id));
        verify(estoqueRepository, never()).deletarPorId(id);
    }
}
