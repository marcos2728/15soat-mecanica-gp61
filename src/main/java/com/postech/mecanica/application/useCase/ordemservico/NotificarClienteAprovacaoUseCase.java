package com.postech.mecanica.application.useCase.ordemservico;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.postech.mecanica.domain.entity.Cliente;
import com.postech.mecanica.domain.entity.OrdemServico;
import com.postech.mecanica.domain.repository.ClienteRepository;
import com.postech.mecanica.domain.repository.OrdemServicoRepository;
import com.postech.mecanica.domain.service.IEmailService;
import com.postech.mecanica.exception.ResourceNotFoundException;

@Service
public class NotificarClienteAprovacaoUseCase {

    private static final Logger log = LoggerFactory.getLogger(NotificarClienteAprovacaoUseCase.class);

    private final OrdemServicoRepository ordemServicoRepository;
    private final ClienteRepository clienteRepository;
    private final IEmailService emailService;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public NotificarClienteAprovacaoUseCase(
            OrdemServicoRepository ordemServicoRepository,
            ClienteRepository clienteRepository,
            IEmailService emailService) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.clienteRepository = clienteRepository;
        this.emailService = emailService;
    }

    public void executar(Long ordemServicoId) {
        OrdemServico os = ordemServicoRepository.consultarPorId(ordemServicoId)
                .orElseThrow(() -> new ResourceNotFoundException("Ordem de serviço não encontrada com ID: " + ordemServicoId));

        if (os.getVeiculo() == null || os.getVeiculo().getId() == null) {
            log.warn("OS #{} sem veículo associado. Notificação não pode ser enviada.", ordemServicoId);
            return;
        }

        Cliente cliente = clienteRepository.consultarPorVeiculoId(os.getVeiculo().getId())
                .orElse(null);

        if (cliente == null) {
            log.warn("Nenhum cliente associado ao veículo ID {} da OS #{}. Notificação não enviada.",
                    os.getVeiculo().getId(), ordemServicoId);
            return;
        }

        String linkAprovar = normalizarUrl(baseUrl) + "/ordens-servicos/" + os.getId() + "/aprovar";
        String linkRecusar = normalizarUrl(baseUrl) + "/ordens-servicos/" + os.getId() + "/recusar";

        emailService.enviarNotificacaoOrcamento(cliente, os, linkAprovar, linkRecusar);
        log.info("Notificação de orçamento da OS #{} enviada com sucesso para o cliente {}", os.getId(), cliente.getEmail());
    }

    private String normalizarUrl(String url) {
        if (url == null || url.isBlank()) {
            return "http://localhost:8080";
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}
