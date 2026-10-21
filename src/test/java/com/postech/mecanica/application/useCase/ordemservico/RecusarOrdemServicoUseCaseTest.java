package com.postech.mecanica.application.useCase.ordemservico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.postech.mecanica.application.dto.OrdemServicoResponseDTO;
import com.postech.mecanica.application.mapper.OrdemServicoMapper;
import com.postech.mecanica.domain.entity.Estoque;
import com.postech.mecanica.domain.entity.OrdemServico;
import com.postech.mecanica.domain.entity.OrdemServicoItemEstoque;
import com.postech.mecanica.domain.entity.Veiculo;
import com.postech.mecanica.domain.enums.StatusOrdemServico;
import com.postech.mecanica.domain.repository.EstoqueRepository;
import com.postech.mecanica.domain.repository.OrdemServicoRepository;
import com.postech.mecanica.domain.vo.Placa;
import com.postech.mecanica.exception.RegraNegocioException;
import com.postech.mecanica.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class RecusarOrdemServicoUseCaseTest {

    @Mock
    private OrdemServicoRepository ordemServicoRepository;

    @Mock
    private EstoqueRepository estoqueRepository;

    @Mock
    private OrdemServicoMapper ordemServicoMapper;

    @InjectMocks
    private RecusarOrdemServicoUseCase recusarOrdemServicoUseCase;

    private OrdemServico ordemServico;
    private Veiculo veiculo;
    private Estoque estoque;

    @BeforeEach
    void setUp() {
        veiculo = new Veiculo(1L, new Placa("ABC1D23"), "Onix", "Chevrolet", 2021);
        OrdemServicoItemEstoque peca = new OrdemServicoItemEstoque(10L, "Filtro de óleo", 2, 35.90, true);
        estoque = new Estoque(10L, "Filtro de óleo", 5, 35.90);

        ordemServico = new OrdemServico(
                1L,
                veiculo,
                StatusOrdemServico.PENDENTE_APROVACAO_CLIENTE,
                "Troca de óleo",
                List.of(peca),
                List.of(),
                71.80,
                LocalDateTime.now(),
                null
        );
    }

    @Test
    @DisplayName("Deve recusar a OS com sucesso, transitar para CANCELADA e estornar o estoque")
    void deveRecusarEEstornarEstoqueComSucesso() {
        when(ordemServicoRepository.consultarPorId(1L)).thenReturn(Optional.of(ordemServico));
        when(estoqueRepository.consultarPorId(10L)).thenReturn(Optional.of(estoque));
        when(ordemServicoRepository.salvar(any(OrdemServico.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrdemServicoResponseDTO responseDTO = new OrdemServicoResponseDTO(
                1L, 1L, "ABC1D23", StatusOrdemServico.CANCELADA, "Troca de óleo", List.of(), List.of(), 71.80, LocalDateTime.now(), LocalDateTime.now()
        );
        when(ordemServicoMapper.toResponseDTO(any(OrdemServico.class))).thenReturn(responseDTO);

        OrdemServicoResponseDTO resultado = recusarOrdemServicoUseCase.executar(1L);

        assertNotNull(resultado);
        assertEquals(StatusOrdemServico.CANCELADA, resultado.status());
        assertEquals(StatusOrdemServico.CANCELADA, ordemServico.getStatus());

        // Verifica que o estoque foi devolvido (5 + 2 = 7)
        assertEquals(7, estoque.getQuantidadeItem());
        verify(estoqueRepository).salvar(estoque);
        verify(ordemServicoRepository).salvar(ordemServico);
    }

    @Test
    @DisplayName("Deve retornar idêntico quando a OS já estiver CANCELADA")
    void deveSerIdempotenteQuandoJaCancelada() {
        ordemServico = new OrdemServico(
                1L,
                veiculo,
                StatusOrdemServico.CANCELADA,
                "Cancelado",
                List.of(),
                List.of(),
                0.0,
                LocalDateTime.now(),
                null
        );

        when(ordemServicoRepository.consultarPorId(1L)).thenReturn(Optional.of(ordemServico));
        OrdemServicoResponseDTO responseDTO = new OrdemServicoResponseDTO(
                1L, 1L, "ABC1D23", StatusOrdemServico.CANCELADA, "Cancelado", List.of(), List.of(), 0.0, LocalDateTime.now(), null
        );
        when(ordemServicoMapper.toResponseDTO(ordemServico)).thenReturn(responseDTO);

        OrdemServicoResponseDTO resultado = recusarOrdemServicoUseCase.executar(1L);

        assertEquals(StatusOrdemServico.CANCELADA, resultado.status());
        verify(ordemServicoRepository, never()).salvar(any());
        verify(estoqueRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException quando a OS não estiver aguardando aprovação")
    void deveLancarExcecaoQuandoStatusInvalido() {
        ordemServico = new OrdemServico(
                1L,
                veiculo,
                StatusOrdemServico.EM_EXECUCAO,
                "Em andamento",
                List.of(),
                List.of(),
                100.0,
                LocalDateTime.now(),
                null
        );

        when(ordemServicoRepository.consultarPorId(1L)).thenReturn(Optional.of(ordemServico));

        assertThrows(RegraNegocioException.class, () -> recusarOrdemServicoUseCase.executar(1L));
        verify(ordemServicoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando a OS não existir")
    void deveLancarExcecaoQuandoNaoEncontrada() {
        when(ordemServicoRepository.consultarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> recusarOrdemServicoUseCase.executar(99L));
    }
}
