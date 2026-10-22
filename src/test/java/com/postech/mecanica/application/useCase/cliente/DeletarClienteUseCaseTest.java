package com.postech.mecanica.application.useCase.cliente;

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

import com.postech.mecanica.domain.repository.ClienteRepository;
import com.postech.mecanica.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class DeletarClienteUseCaseTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private DeletarClienteUseCase deletarClienteUseCase;

    @Test
    @DisplayName("Deve deletar cliente existente com sucesso")
    void deveDeletarClienteComSucesso() {
        Long id = 1L;
        when(clienteRepository.existsById(id)).thenReturn(true);

        assertDoesNotThrow(() -> deletarClienteUseCase.executar(id));
        verify(clienteRepository).deletarPorId(id);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando tentar deletar cliente inexistente")
    void deveLancarExcecaoAoDeletarClienteInexistente() {
        Long id = 99L;
        when(clienteRepository.existsById(id)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> deletarClienteUseCase.executar(id));
        verify(clienteRepository, never()).deletarPorId(id);
    }
}
