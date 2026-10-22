package com.postech.mecanica.application.useCase.veiculo;

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

import com.postech.mecanica.domain.repository.VeiculoRepository;
import com.postech.mecanica.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class DeletarVeiculoUseCaseTest {

    @Mock
    private VeiculoRepository veiculoRepository;

    @InjectMocks
    private DeletarVeiculoUseCase deletarVeiculoUseCase;

    @Test
    @DisplayName("Deve deletar veículo existente com sucesso")
    void deveDeletarVeiculoComSucesso() {
        Long id = 1L;
        when(veiculoRepository.existsById(id)).thenReturn(true);

        assertDoesNotThrow(() -> deletarVeiculoUseCase.executar(id));
        verify(veiculoRepository).deletarPorId(id);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao tentar deletar veículo inexistente")
    void deveLancarExcecaoAoDeletarVeiculoInexistente() {
        Long id = 99L;
        when(veiculoRepository.existsById(id)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> deletarVeiculoUseCase.executar(id));
        verify(veiculoRepository, never()).deletarPorId(id);
    }
}
