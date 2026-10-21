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
import com.postech.mecanica.domain.entity.OrdemServico;
import com.postech.mecanica.domain.entity.Veiculo;
import com.postech.mecanica.domain.enums.StatusOrdemServico;
import com.postech.mecanica.domain.repository.OrdemServicoRepository;
import com.postech.mecanica.domain.vo.Placa;
import com.postech.mecanica.exception.RegraNegocioException;
import com.postech.mecanica.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class AprovarOrdemServicoUseCaseTest {

    @Mock
    private OrdemServicoRepository ordemServicoRepository;

    @Mock
    private OrdemServicoMapper ordemServicoMapper;

    @InjectMocks
    private AprovarOrdemServicoUseCase aprovarOrdemServicoUseCase;

    private OrdemServico ordemServico;
    private Veiculo veiculo;

    @BeforeEach
    void setUp() {
        veiculo = new Veiculo(1L, new Placa("ABC1D23"), "Onix", "Chevrolet", 2021);
        ordemServico = new OrdemServico(
                1L,
                veiculo,
                StatusOrdemServico.PENDENTE_APROVACAO_CLIENTE,
                "Revisão geral",
                List.of(),
                List.of(),
                250.0,
                LocalDateTime.now(),
                null
        );
    }

    @Test
    @DisplayName("Deve aprovar a ordem de serviço com sucesso quando status for PENDENTE_APROVACAO_CLIENTE")
    void deveAprovarComSucesso() {
        when(ordemServicoRepository.consultarPorId(1L)).thenReturn(Optional.of(ordemServico));
        when(ordemServicoRepository.salvar(any(OrdemServico.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrdemServicoResponseDTO responseDTO = new OrdemServicoResponseDTO(
                1L, 1L, "ABC1D23", StatusOrdemServico.APROVADA, "Revisão geral", List.of(), List.of(), 250.0, LocalDateTime.now(), LocalDateTime.now()
        );
        when(ordemServicoMapper.toResponseDTO(any(OrdemServico.class))).thenReturn(responseDTO);

        OrdemServicoResponseDTO resultado = aprovarOrdemServicoUseCase.executar(1L);

        assertNotNull(resultado);
        assertEquals(StatusOrdemServico.APROVADA, resultado.status());
        verify(ordemServicoRepository).salvar(ordemServico);
        assertEquals(StatusOrdemServico.APROVADA, ordemServico.getStatus());
    }

    @Test
    @DisplayName("Deve retornar idêntico quando a OS já estiver APROVADA")
    void deveSerIdempotenteQuandoJaAprovada() {
        ordemServico = new OrdemServico(
                1L,
                veiculo,
                StatusOrdemServico.APROVADA,
                "Revisão",
                List.of(),
                List.of(),
                200.0,
                LocalDateTime.now(),
                null
        );

        when(ordemServicoRepository.consultarPorId(1L)).thenReturn(Optional.of(ordemServico));
        OrdemServicoResponseDTO responseDTO = new OrdemServicoResponseDTO(
                1L, 1L, "ABC1D23", StatusOrdemServico.APROVADA, "Revisão", List.of(), List.of(), 200.0, LocalDateTime.now(), null
        );
        when(ordemServicoMapper.toResponseDTO(ordemServico)).thenReturn(responseDTO);

        OrdemServicoResponseDTO resultado = aprovarOrdemServicoUseCase.executar(1L);

        assertEquals(StatusOrdemServico.APROVADA, resultado.status());
        verify(ordemServicoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException quando a OS não estiver aguardando aprovação")
    void deveLancarExcecaoQuandoStatusInvalidoParaAprovacao() {
        ordemServico = new OrdemServico(
                1L,
                veiculo,
                StatusOrdemServico.EM_DIAGNOSTICO,
                "Revisão",
                List.of(),
                List.of(),
                200.0,
                LocalDateTime.now(),
                null
        );

        when(ordemServicoRepository.consultarPorId(1L)).thenReturn(Optional.of(ordemServico));

        assertThrows(RegraNegocioException.class, () -> aprovarOrdemServicoUseCase.executar(1L));
        verify(ordemServicoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando a OS não for encontrada")
    void deveLancarExcecaoQuandoNaoEncontrada() {
        when(ordemServicoRepository.consultarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> aprovarOrdemServicoUseCase.executar(99L));
    }
}
