package com.postech.mecanica.application.useCase.estoque;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.List;

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

@ExtendWith(MockitoExtension.class)
class ConsultarTodasItensEstoqueUseCaseTest {

    @Mock
    private EstoqueRepository estoqueRepository;

    @Mock
    private EstoqueMapper estoqueMapper;

    @InjectMocks
    private ConsultarTodasItensEstoqueUseCase consultarTodasItensEstoqueUseCase;

    @Test
    @DisplayName("Deve consultar todos os itens de estoque com sucesso")
    void deveConsultarTodosItensComSucesso() {
        Estoque item1 = new Estoque(1L, "Óleo", 10, 50.0);
        Estoque item2 = new Estoque(2L, "Filtro", 5, 30.0);
        EstoqueResponseDTO dto1 = new EstoqueResponseDTO(1L, "Óleo", 10, 50.0, List.of());
        EstoqueResponseDTO dto2 = new EstoqueResponseDTO(2L, "Filtro", 5, 30.0, List.of());

        when(estoqueRepository.consultarTodos()).thenReturn(List.of(item1, item2));
        when(estoqueMapper.toResponseDTO(item1)).thenReturn(dto1);
        when(estoqueMapper.toResponseDTO(item2)).thenReturn(dto2);

        List<EstoqueResponseDTO> resultado = consultarTodasItensEstoqueUseCase.executar();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
    }
}
