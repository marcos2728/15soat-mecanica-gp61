package com.postech.mecanica.application.useCase.ordemservico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.postech.mecanica.application.dto.OrdemServicoResponseDTO;
import com.postech.mecanica.application.mapper.OrdemServicoMapper;
import com.postech.mecanica.domain.entity.OrdemServico;
import com.postech.mecanica.domain.entity.Veiculo;
import com.postech.mecanica.domain.enums.StatusOrdemServico;
import com.postech.mecanica.domain.event.OrdemServicoAguardandoAprovacaoEvent;
import com.postech.mecanica.domain.repository.OrdemServicoRepository;
import com.postech.mecanica.domain.vo.Placa;
import com.postech.mecanica.exception.RegraNegocioException;

@ExtendWith(MockitoExtension.class)
class AtualizarStatusOrdemUseCaseTest {

    @Mock
    private OrdemServicoRepository ordemServicoRepository;

    @Mock
    private OrdemServicoMapper ordemServicoMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private AtualizarStatusOrdemUseCase useCase;

    private OrdemServico ordemServico;

    @BeforeEach
    void setUp() {
        Veiculo veiculo = new Veiculo(1L, new Placa("ABC1D23"), "Corsa", "Chevrolet", 2015);
        ordemServico = new OrdemServico(1L, veiculo, StatusOrdemServico.CRIADA, "Barulho", new ArrayList<>(), new ArrayList<>(), 0.0, LocalDateTime.now(), null);
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao tentar transitar diretamente para DIAGNOSTICO_CONCLUIDO")
    void deveLancarExcecaoParaDiagnosticoConcluido() {
        assertThrows(RegraNegocioException.class, () -> useCase.executar(1L, StatusOrdemServico.DIAGNOSTICO_CONCLUIDO));
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException quando a ordem de serviço não for encontrada")
    void deveLancarExcecaoQuandoNaoEncontrada() {
        when(ordemServicoRepository.consultarPorId(1L)).thenReturn(Optional.empty());

        assertThrows(RegraNegocioException.class, () -> useCase.executar(1L, StatusOrdemServico.EM_DIAGNOSTICO));
        verify(ordemServicoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve atualizar status normalmente (CRIADA -> EM_DIAGNOSTICO)")
    void deveAtualizarStatusNormalmente() {
        when(ordemServicoRepository.consultarPorId(1L)).thenReturn(Optional.of(ordemServico));
        when(ordemServicoRepository.salvar(any(OrdemServico.class))).thenAnswer(inv -> inv.getArgument(0));

        OrdemServicoResponseDTO responseDTO = new OrdemServicoResponseDTO(1L, 1L, "ABC1D23", StatusOrdemServico.EM_DIAGNOSTICO, "Barulho", null, null, 0.0, LocalDateTime.now(), null);
        when(ordemServicoMapper.toResponseDTO(any(OrdemServico.class))).thenReturn(responseDTO);

        OrdemServicoResponseDTO resultado = useCase.executar(1L, StatusOrdemServico.EM_DIAGNOSTICO);

        assertNotNull(resultado);
        assertEquals(StatusOrdemServico.EM_DIAGNOSTICO, resultado.status());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("Deve disparar evento ao transitar para PENDENTE_APROVACAO_CLIENTE")
    void deveDispararEventoAguardandoAprovacao() {
        ordemServico = new OrdemServico(1L, ordemServico.getVeiculo(), StatusOrdemServico.DIAGNOSTICO_CONCLUIDO, "Barulho", new ArrayList<>(), new ArrayList<>(), 0.0, LocalDateTime.now(), null);
        when(ordemServicoRepository.consultarPorId(1L)).thenReturn(Optional.of(ordemServico));
        when(ordemServicoRepository.salvar(any(OrdemServico.class))).thenAnswer(inv -> inv.getArgument(0));

        OrdemServicoResponseDTO responseDTO = new OrdemServicoResponseDTO(1L, 1L, "ABC1D23", StatusOrdemServico.PENDENTE_APROVACAO_CLIENTE, "Barulho", null, null, 0.0, LocalDateTime.now(), null);
        when(ordemServicoMapper.toResponseDTO(any(OrdemServico.class))).thenReturn(responseDTO);

        OrdemServicoResponseDTO resultado = useCase.executar(1L, StatusOrdemServico.PENDENTE_APROVACAO_CLIENTE);

        assertNotNull(resultado);
        verify(eventPublisher).publishEvent(any(OrdemServicoAguardandoAprovacaoEvent.class));
        verify(ordemServicoRepository, times(1)).salvar(any());
    }

    @Test
    @DisplayName("Deve disparar evento e transitar automaticamente para PENDENTE_APROVACAO_CLIENTE quando status for PENDENTE_NOTIFICACAO_CLIENTE")
    void deveTransitarAutomaticamentePendenteNotificacao() {
        ordemServico = new OrdemServico(1L, ordemServico.getVeiculo(), StatusOrdemServico.DIAGNOSTICO_CONCLUIDO, "Barulho", new ArrayList<>(), new ArrayList<>(), 0.0, LocalDateTime.now(), null);
        when(ordemServicoRepository.consultarPorId(1L)).thenReturn(Optional.of(ordemServico));
        when(ordemServicoRepository.salvar(any(OrdemServico.class))).thenAnswer(inv -> inv.getArgument(0));

        OrdemServicoResponseDTO responseDTO = new OrdemServicoResponseDTO(1L, 1L, "ABC1D23", StatusOrdemServico.PENDENTE_APROVACAO_CLIENTE, "Barulho", null, null, 0.0, LocalDateTime.now(), null);
        when(ordemServicoMapper.toResponseDTO(any(OrdemServico.class))).thenReturn(responseDTO);

        OrdemServicoResponseDTO resultado = useCase.executar(1L, StatusOrdemServico.PENDENTE_NOTIFICACAO_CLIENTE);

        assertNotNull(resultado);
        verify(eventPublisher).publishEvent(any(OrdemServicoAguardandoAprovacaoEvent.class));
        verify(ordemServicoRepository, times(2)).salvar(any());
    }
}
