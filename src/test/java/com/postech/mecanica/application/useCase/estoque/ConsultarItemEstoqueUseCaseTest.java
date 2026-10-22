package com.postech.mecanica.application.useCase.estoque;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.postech.mecanica.application.dto.EstoqueResponseDTO;
import com.postech.mecanica.application.mapper.EstoqueMapper;
import com.postech.mecanica.domain.entity.Estoque;
import com.postech.mecanica.domain.repository.EstoqueRepository;
import com.postech.mecanica.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class ConsultarItemEstoqueUseCaseTest {

    @Mock
    private EstoqueRepository estoqueRepository;

    @Mock
    private EstoqueMapper estoqueMapper;

    @InjectMocks
    private ConsultarItemEstoqueUseCase consultarItemEstoqueUseCase;

    @Test
    @DisplayName("Deve consultar item de estoque por ID com sucesso")
    void deveConsultarItemEstoquePorIdComSucesso() {
        Long id = 1L;
        Estoque estoque = new Estoque(id, "Vela de ignição", 20, 25.0);
        EstoqueResponseDTO responseDTO = new EstoqueResponseDTO(id, "Vela de ignição", 20, 25.0, List.of());

        when(estoqueRepository.consultarPorId(id)).thenReturn(Optional.of(estoque));
        when(estoqueMapper.toResponseDTO(estoque)).thenReturn(responseDTO);

        EstoqueResponseDTO resultado = consultarItemEstoqueUseCase.executar(id);

        assertNotNull(resultado);
        assertEquals(id, resultado.id());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando item de estoque não for encontrado")
    void deveLancarExcecaoQuandoNaoEncontrado() {
        Long id = 99L;
        when(estoqueRepository.consultarPorId(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> consultarItemEstoqueUseCase.executar(id));
    }
}
