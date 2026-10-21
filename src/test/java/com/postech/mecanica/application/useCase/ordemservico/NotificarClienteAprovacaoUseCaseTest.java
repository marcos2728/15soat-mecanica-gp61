package com.postech.mecanica.application.useCase.ordemservico;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
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
import org.springframework.test.util.ReflectionTestUtils;

import com.postech.mecanica.domain.entity.Cliente;
import com.postech.mecanica.domain.entity.OrdemServico;
import com.postech.mecanica.domain.entity.Veiculo;
import com.postech.mecanica.domain.enums.StatusOrdemServico;
import com.postech.mecanica.domain.repository.ClienteRepository;
import com.postech.mecanica.domain.repository.OrdemServicoRepository;
import com.postech.mecanica.domain.service.IEmailService;
import com.postech.mecanica.domain.vo.CpfCnpj;
import com.postech.mecanica.domain.vo.Placa;

@ExtendWith(MockitoExtension.class)
class NotificarClienteAprovacaoUseCaseTest {

    @Mock
    private OrdemServicoRepository ordemServicoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private IEmailService emailService;

    @InjectMocks
    private NotificarClienteAprovacaoUseCase notificarClienteAprovacaoUseCase;

    private OrdemServico ordemServico;
    private Cliente cliente;
    private Veiculo veiculo;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(notificarClienteAprovacaoUseCase, "baseUrl", "http://localhost:8080");

        veiculo = new Veiculo(10L, new Placa("ABC1D23"), "Onix", "Chevrolet", 2021);
        cliente = new Cliente(
                1L,
                new CpfCnpj("12345678901"),
                "João da Silva",
                "joao.silva@email.com",
                "11987654321",
                "1990-05-14",
                List.of(veiculo)
        );

        ordemServico = new OrdemServico(
                100L,
                veiculo,
                StatusOrdemServico.PENDENTE_APROVACAO_CLIENTE,
                "Problema na suspensão",
                List.of(),
                List.of(),
                300.0,
                LocalDateTime.now(),
                null
        );
    }

    @Test
    @DisplayName("Deve gerar os links de ação e invocar o serviço de e-mail com sucesso")
    void deveEnviarNotificacaoComLinksCorretos() {
        when(ordemServicoRepository.consultarPorId(100L)).thenReturn(Optional.of(ordemServico));
        when(clienteRepository.consultarPorVeiculoId(10L)).thenReturn(Optional.of(cliente));

        notificarClienteAprovacaoUseCase.executar(100L);

        verify(emailService).enviarNotificacaoOrcamento(
                eq(cliente),
                eq(ordemServico),
                eq("http://localhost:8080/ordens-servicos/100/aprovar"),
                eq("http://localhost:8080/ordens-servicos/100/recusar")
        );
    }

    @Test
    @DisplayName("Não deve disparar e-mail se o cliente do veículo não for encontrado")
    void naoDeveEnviarSeClienteNaoEncontrado() {
        when(ordemServicoRepository.consultarPorId(100L)).thenReturn(Optional.of(ordemServico));
        when(clienteRepository.consultarPorVeiculoId(10L)).thenReturn(Optional.empty());

        notificarClienteAprovacaoUseCase.executar(100L);

        verify(emailService, never()).enviarNotificacaoOrcamento(any(), any(), any(), any());
    }
}
