package com.postech.mecanica.application.useCase.servico;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.postech.mecanica.domain.repository.ServicoRepository;
import com.postech.mecanica.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class DeletarServicoUseCaseTest {

    @Mock
    private ServicoRepository servicoRepository;

    @InjectMocks
    private DeletarServicoUseCase deletarServicoUseCase;

    @Test
    @DisplayName("Deve deletar serviço existente com sucesso")
    void deveDeletarServicoComSucesso() {
        Long id = 1L;
        when(servicoRepository.existsById(id)).thenReturn(true);

        assertDoesNotThrow(() -> deletarServicoUseCase.executar(id));
        verify(servicoRepository).deletarPorId(id);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando serviço não existir")
    void deveLancarExcecaoQuandoNaoExistir() {
        Long id = 99L;
        when(servicoRepository.existsById(id)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> deletarServicoUseCase.executar(id));
        verify(servicoRepository, never()).deletarPorId(id);
    }
}
