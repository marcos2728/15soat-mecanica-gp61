package com.postech.mecanica.infrastructure.email;

import java.text.NumberFormat;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.postech.mecanica.domain.entity.Cliente;
import com.postech.mecanica.domain.entity.OrdemServico;
import com.postech.mecanica.domain.entity.OrdemServicoItemEstoque;
import com.postech.mecanica.domain.entity.OrdemServicoItemServico;
import com.postech.mecanica.domain.service.IEmailService;

import jakarta.mail.internet.MimeMessage;

@Service
public class SmtpEmailService implements IEmailService {

    private static final Logger log = LoggerFactory.getLogger(SmtpEmailService.class);
    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Value("${app.mail.from:noreply@mecanica.com}")
    private String remetente;

    public SmtpEmailService(ObjectProvider<JavaMailSender> mailSenderProvider) {
        this.mailSenderProvider = mailSenderProvider;
    }

    @Override
    public void enviarNotificacaoOrcamento(Cliente cliente, OrdemServico ordemServico, String linkAprovar,
            String linkRecusar) {
        if (cliente == null || cliente.getEmail() == null || cliente.getEmail().isBlank()) {
            log.warn("Cliente sem e-mail cadastrado para a OS #{}. Notificação não enviada.",
                    ordemServico != null ? ordemServico.getId() : "N/A");
            return;
        }

        String assunto = "Orçamento Disponível para Aprovação - OS #" + ordemServico.getId();
        String html = construirTemplateHtml(cliente, ordemServico, linkAprovar, linkRecusar);

        enviarEmail(cliente.getEmail(), assunto, html);
    }

    @Override
    public void enviarEmail(String destinatario, String assunto, String corpoHtml) {
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();

        if (mailSender == null) {
            log.warn("[SIMULAÇÃO SMTP] Provedor de e-mail não configurado. Mensagem simulada para '{}' com assunto '{}'",
                    destinatario, assunto);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(remetente);
            helper.setTo(destinatario);
            helper.setSubject(assunto);
            helper.setText(corpoHtml, true);

            mailSender.send(message);
            log.info("E-mail de notificação enviado com sucesso para: {}", destinatario);
        } catch (Exception e) {
            log.error("Falha ao enviar e-mail via SMTP para {}: {}", destinatario, e.getMessage(), e);
        }
    }

    private String construirTemplateHtml(Cliente cliente, OrdemServico os, String linkAprovar, String linkRecusar) {
        NumberFormat moeda = NumberFormat.getCurrencyInstance(PT_BR);
        String valorTotalStr = moeda.format(os.getValorTotal() != null ? os.getValorTotal() : 0.0);

        StringBuilder pecasHtml = new StringBuilder();
        if (os.getPecas() != null && !os.getPecas().isEmpty()) {
            for (OrdemServicoItemEstoque peca : os.getPecas()) {
                double subtotal = (peca.getQuantidade() != null && peca.getValorUnitario() != null)
                        ? peca.getQuantidade() * peca.getValorUnitario()
                        : 0.0;
                pecasHtml.append("<tr>")
                        .append("<td style='padding: 8px 12px; border-bottom: 1px solid #e2e8f0;'>").append(peca.getNomePeca() != null ? peca.getNomePeca() : "Item #" + peca.getEstoqueId()).append("</td>")
                        .append("<td style='padding: 8px 12px; text-align: center; border-bottom: 1px solid #e2e8f0;'>").append(peca.getQuantidade()).append("</td>")
                        .append("<td style='padding: 8px 12px; text-align: right; border-bottom: 1px solid #e2e8f0;'>").append(moeda.format(peca.getValorUnitario())).append("</td>")
                        .append("<td style='padding: 8px 12px; text-align: right; border-bottom: 1px solid #e2e8f0;'>").append(moeda.format(subtotal)).append("</td>")
                        .append("</tr>");
            }
        } else {
            pecasHtml.append("<tr><td colspan='4' style='padding: 8px 12px; text-align: center; color: #64748b;'>Nenhuma peça adicionada.</td></tr>");
        }

        StringBuilder servicosHtml = new StringBuilder();
        if (os.getServicos() != null && !os.getServicos().isEmpty()) {
            for (OrdemServicoItemServico servico : os.getServicos()) {
                servicosHtml.append("<tr>")
                        .append("<td style='padding: 8px 12px; border-bottom: 1px solid #e2e8f0;'>").append(servico.getNomeServico()).append("</td>")
                        .append("<td style='padding: 8px 12px; text-align: right; border-bottom: 1px solid #e2e8f0;'>").append(moeda.format(servico.getValorAplicado())).append("</td>")
                        .append("</tr>");
            }
        } else {
            servicosHtml.append("<tr><td colspan='2' style='padding: 8px 12px; text-align: center; color: #64748b;'>Nenhum serviço adicionado.</td></tr>");
        }

        String veiculoInfo = os.getVeiculo() != null
                ? String.format("%s %s (Placa: %s, Ano: %s)",
                        os.getVeiculo().getMarca() != null ? os.getVeiculo().getMarca() : "",
                        os.getVeiculo().getModelo() != null ? os.getVeiculo().getModelo() : "",
                        os.getVeiculo().getPlaca() != null ? os.getVeiculo().getPlaca().placa() : "",
                        os.getVeiculo().getAnoFabricacao() != null ? os.getVeiculo().getAnoFabricacao() : "")
                : "Não informado";

        return """
            <!DOCTYPE html>
            <html lang="pt-BR">
            <head>
              <meta charset="UTF-8">
              <meta name="viewport" content="width=device-width, initial-scale=1.0">
              <title>Orçamento da Ordem de Serviço</title>
            </head>
            <body style="margin: 0; padding: 24px; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; background-color: #f8fafc; color: #1e293b;">
              <div style="max-width: 640px; margin: 0 auto; background-color: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -2px rgba(0, 0, 0, 0.1);">
                <!-- Header -->
                <div style="background: linear-gradient(135deg, #1e3a8a 0%%, #2563eb 100%%); padding: 28px 32px; color: #ffffff;">
                  <h1 style="margin: 0; font-size: 24px; font-weight: 700; letter-spacing: -0.02em;">Oficina Mecânica FIAP</h1>
                  <p style="margin: 6px 0 0 0; font-size: 14px; opacity: 0.9;">Orçamento Pronto para Avaliação • OS #%d</p>
                </div>

                <!-- Conteúdo -->
                <div style="padding: 32px;">
                  <p style="font-size: 16px; margin: 0 0 16px 0;">Olá, <strong>%s</strong>!</p>
                  <p style="font-size: 14px; color: #475569; line-height: 1.6; margin: 0 0 24px 0;">
                    O diagnóstico do seu veículo foi concluído com sucesso. Abaixo constam os detalhes do orçamento para sua revisão e aprovação.
                  </p>

                  <!-- Informações do Veículo -->
                  <div style="background-color: #f1f5f9; border-radius: 8px; padding: 16px; margin-bottom: 24px; border-left: 4px solid #2563eb;">
                    <div style="font-size: 12px; font-weight: 600; text-transform: uppercase; color: #64748b; margin-bottom: 4px;">Veículo</div>
                    <div style="font-size: 15px; font-weight: 600; color: #0f172a;">%s</div>
                    %s
                  </div>

                  <!-- Tabela Peças -->
                  <div style="margin-bottom: 24px;">
                    <h3 style="font-size: 15px; font-weight: 600; color: #0f172a; margin: 0 0 12px 0;">Peças e Componentes</h3>
                    <table style="width: 100%%; border-collapse: collapse; font-size: 13px;">
                      <thead>
                        <tr style="background-color: #f8fafc; color: #475569; text-align: left;">
                          <th style="padding: 8px 12px; border-bottom: 2px solid #cbd5e1;">Item</th>
                          <th style="padding: 8px 12px; text-align: center; border-bottom: 2px solid #cbd5e1;">Qtd</th>
                          <th style="padding: 8px 12px; text-align: right; border-bottom: 2px solid #cbd5e1;">Unitário</th>
                          <th style="padding: 8px 12px; text-align: right; border-bottom: 2px solid #cbd5e1;">Subtotal</th>
                        </tr>
                      </thead>
                      <tbody>
                        %s
                      </tbody>
                    </table>
                  </div>

                  <!-- Tabela Serviços -->
                  <div style="margin-bottom: 24px;">
                    <h3 style="font-size: 15px; font-weight: 600; color: #0f172a; margin: 0 0 12px 0;">Serviços Mecânicos</h3>
                    <table style="width: 100%%; border-collapse: collapse; font-size: 13px;">
                      <thead>
                        <tr style="background-color: #f8fafc; color: #475569; text-align: left;">
                          <th style="padding: 8px 12px; border-bottom: 2px solid #cbd5e1;">Serviço</th>
                          <th style="padding: 8px 12px; text-align: right; border-bottom: 2px solid #cbd5e1;">Valor</th>
                        </tr>
                      </thead>
                      <tbody>
                        %s
                      </tbody>
                    </table>
                  </div>

                  <!-- Total -->
                  <div style="background-color: #ecfdf5; border: 1px solid #a7f3d0; border-radius: 8px; padding: 16px 20px; display: flex; justify-content: space-between; align-items: center; margin-bottom: 32px;">
                    <span style="font-size: 16px; font-weight: 600; color: #065f46;">Valor Total do Orçamento:</span>
                    <span style="font-size: 22px; font-weight: 800; color: #047857;">%s</span>
                  </div>

                  <!-- Ações do Cliente -->
                  <div style="text-align: center; margin-bottom: 28px;">
                    <p style="font-size: 14px; font-weight: 600; color: #334155; margin-bottom: 16px;">
                      Como deseja proceder com este orçamento?
                    </p>
                    <div style="display: inline-block;">
                      <a href="%s" style="background-color: #16a34a; color: #ffffff; text-decoration: none; padding: 14px 28px; border-radius: 8px; font-size: 14px; font-weight: 700; display: inline-block; margin: 6px; box-shadow: 0 2px 4px rgba(22, 163, 74, 0.3);">
                        ✓ APROVAR ORÇAMENTO
                      </a>
                      <a href="%s" style="background-color: #dc2626; color: #ffffff; text-decoration: none; padding: 14px 28px; border-radius: 8px; font-size: 14px; font-weight: 700; display: inline-block; margin: 6px; box-shadow: 0 2px 4px rgba(220, 38, 38, 0.3);">
                        ✕ RECUSAR ORÇAMENTO
                      </a>
                    </div>
                  </div>

                  <p style="font-size: 12px; color: #94a3b8; text-align: center; margin: 0; line-height: 1.5;">
                    Ao clicar no botão de aprovação ou recusa, sua resposta será registrada automaticamente no sistema.<br>
                    Você também pode responder via API REST utilizando o endpoint correspondente.
                  </p>
                </div>

                <!-- Footer -->
                <div style="background-color: #f8fafc; border-top: 1px solid #e2e8f0; padding: 16px 32px; text-align: center; font-size: 12px; color: #94a3b8;">
                  Mecânica FIAP • Tech Challenge Fase 2 • Sistema Integrado de Ordens de Serviço
                </div>
              </div>
            </body>
            </html>
            """.formatted(
                os.getId(),
                cliente.getNome() != null ? cliente.getNome() : "Cliente",
                veiculoInfo,
                os.getDescricaoProblema() != null && !os.getDescricaoProblema().isBlank()
                        ? "<div style='font-size: 13px; color: #475569; margin-top: 6px;'><strong>Problema relatado:</strong> " + os.getDescricaoProblema() + "</div>"
                        : "",
                pecasHtml.toString(),
                servicosHtml.toString(),
                valorTotalStr,
                linkAprovar,
                linkRecusar
        );
    }
}
