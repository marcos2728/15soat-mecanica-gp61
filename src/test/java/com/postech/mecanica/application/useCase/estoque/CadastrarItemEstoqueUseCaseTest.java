package com.postech.mecanica.application.useCase.estoque;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.postech.mecanica.application.dto.EstoqueRequestDTO;
import com.postech.mecanica.application.dto.EstoqueResponseDTO;
import com.postech.mecanica.application.mapper.EstoqueMapper;
import com.postech.mecanica.domain.entity.Estoque;
import com.postech.mecanica.domain.repository.EstoqueRepository;

@ExtendWith(MockitoExtension.class)
class CadastrarItemEstoqueUseCaseTest {

    @Mock
    private EstoqueRepository estoqueRepository;

    @Mock
    private EstoqueMapper estoqueMapper;

    @InjectMocks
    private CadastrarItemEstoqueUseCase cadastrarItemEstoqueUseCase;

    @Test
    @DisplayName("Deve cadastrar item de estoque com sucesso")
    void deveCadastrarItemEstoqueComSucesso() {
        EstoqueRequestDTO request = new EstoqueRequestDTO("Filtro de óleo", 10, 35.0);
        Estoque estoque = new Estoque(1L, "Filtro de óleo", 10, 35.0);
        EstoqueResponseDTO responseDTO = new EstoqueResponseDTO(1L, "Filtro de óleo", 10, 35.0, List.of());

        when(estoqueMapper.toDomain(request)).thenReturn(estoque);
        when(estoqueRepository.salvar(estoque)).thenReturn(estoque);
        when(estoqueMapper.toResponseDTO(estoque)).thenReturn(responseDTO);

        EstoqueResponseDTO resultado = cadastrarItemEstoqueUseCase.executar(request);

        assertNotNull(resultado);
        assertEquals("Filtro de óleo", resultado.nomeItem());
        verify(estoqueRepository).salvar(estoque);
    }
}
