package com.postech.mecanica.infrastructure.email;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import com.postech.mecanica.domain.entity.Cliente;
import com.postech.mecanica.domain.entity.OrdemServico;
import com.postech.mecanica.domain.entity.OrdemServicoItemEstoque;
import com.postech.mecanica.domain.entity.OrdemServicoItemServico;
import com.postech.mecanica.domain.entity.Veiculo;
import com.postech.mecanica.domain.enums.StatusOrdemServico;
import com.postech.mecanica.domain.vo.CpfCnpj;
import com.postech.mecanica.domain.vo.Placa;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;

@ExtendWith(MockitoExtension.class)
class SmtpEmailServiceTest {

    @Mock
    private ObjectProvider<JavaMailSender> mailSenderProvider;

    @Mock
    private JavaMailSender javaMailSender;

    private SmtpEmailService smtpEmailService;
    private Cliente cliente;
    private OrdemServico ordemServico;

    @BeforeEach
    void setUp() {
        smtpEmailService = new SmtpEmailService(mailSenderProvider);
        ReflectionTestUtils.setField(smtpEmailService, "remetente", "noreply@mecanica.com");

        Veiculo veiculo = new Veiculo(1L, new Placa("ABC1D23"), "Corolla", "Toyota", 2020);
        cliente = new Cliente(
                1L,
                new CpfCnpj("12345678901"),
                "João da Silva",
                "joao.silva@email.com",
                "11987654321",
                "1990-05-14",
                List.of(veiculo)
        );

        OrdemServicoItemEstoque peca = new OrdemServicoItemEstoque(1L, "Pastilha de freio", 1, 89.90, true);
        OrdemServicoItemServico servico = new OrdemServicoItemServico(1L, "Troca de pastilhas", 180.00);

        ordemServico = new OrdemServico(
                1L,
                veiculo,
                StatusOrdemServico.PENDENTE_APROVACAO_CLIENTE,
                "Freio chiando",
                List.of(peca),
                List.of(servico),
                269.90,
                LocalDateTime.now(),
                null
        );
    }

    @Test
    @DisplayName("Deve enviar e-mail com sucesso quando JavaMailSender estiver configurado")
    void deveEnviarEmailComSucesso() {
        when(mailSenderProvider.getIfAvailable()).thenReturn(javaMailSender);
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        assertDoesNotThrow(() -> smtpEmailService.enviarNotificacaoOrcamento(
                cliente,
                ordemServico,
                "http://localhost:8080/ordens-servicos/1/aprovar",
                "http://localhost:8080/ordens-servicos/1/recusar"
        ));

        verify(javaMailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Não deve lançar exceção quando JavaMailSender for nulo (modo simulação)")
    void deveSimularEnvioQuandoMailSenderNaoConfigurado() {
        when(mailSenderProvider.getIfAvailable()).thenReturn(null);

        assertDoesNotThrow(() -> smtpEmailService.enviarNotificacaoOrcamento(
                cliente,
                ordemServico,
                "http://localhost:8080/ordens-servicos/1/aprovar",
                "http://localhost:8080/ordens-servicos/1/recusar"
        ));
    }

    @Test
    @DisplayName("Não deve enviar e-mail quando cliente for nulo ou sem e-mail")
    void naoDeveEnviarQuandoClienteSemEmail() {
        assertDoesNotThrow(() -> smtpEmailService.enviarNotificacaoOrcamento(
                null,
                ordemServico,
                "http://localhost:8080/ordens-servicos/1/aprovar",
                "http://localhost:8080/ordens-servicos/1/recusar"
        ));

        Cliente clienteSemEmail = new Cliente(1L, new CpfCnpj("12345678901"), "Sem Email", "", "11999999999", "1990-01-01", List.of());
        assertDoesNotThrow(() -> smtpEmailService.enviarNotificacaoOrcamento(
                clienteSemEmail,
                ordemServico,
                "http://localhost:8080/ordens-servicos/1/aprovar",
                "http://localhost:8080/ordens-servicos/1/recusar"
        ));

        verify(javaMailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Deve capturar e tratar erro de envio SMTP sem lançar exceção")
    void deveTratarErroSmtp() {
        when(mailSenderProvider.getIfAvailable()).thenReturn(javaMailSender);
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new MailSendException("Erro de conexão SMTP")).when(javaMailSender).send(any(MimeMessage.class));

        assertDoesNotThrow(() -> smtpEmailService.enviarNotificacaoOrcamento(
                cliente,
                ordemServico,
                "http://localhost:8080/ordens-servicos/1/aprovar",
                "http://localhost:8080/ordens-servicos/1/recusar"
        ));
    }

    @Test
    @DisplayName("Deve gerar template HTML mesmo com peças e serviços vazios e veículo nulo")
    void deveGerarTemplateComListasVazias() {
        when(mailSenderProvider.getIfAvailable()).thenReturn(javaMailSender);
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        OrdemServico osVazia = mock(OrdemServico.class);
        when(osVazia.getId()).thenReturn(2L);
        when(osVazia.getVeiculo()).thenReturn(null);
        when(osVazia.getPecas()).thenReturn(Collections.emptyList());
        when(osVazia.getServicos()).thenReturn(Collections.emptyList());
        when(osVazia.getValorTotal()).thenReturn(null);

        assertDoesNotThrow(() -> smtpEmailService.enviarNotificacaoOrcamento(
                cliente,
                osVazia,
                "http://localhost:8080/aprovar",
                "http://localhost:8080/recusar"
        ));

        verify(javaMailSender).send(mimeMessage);
    }
}
