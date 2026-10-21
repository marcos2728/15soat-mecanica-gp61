package com.postech.mecanica.application.useCase.ordemservico;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postech.mecanica.application.dto.OrdemServicoResponseDTO;
import com.postech.mecanica.application.mapper.OrdemServicoMapper;
import com.postech.mecanica.domain.entity.OrdemServico;
import com.postech.mecanica.domain.enums.StatusOrdemServico;
import com.postech.mecanica.domain.event.OrdemServicoAguardandoAprovacaoEvent;
import com.postech.mecanica.domain.repository.OrdemServicoRepository;
import com.postech.mecanica.exception.RegraNegocioException;

@Service
public class AtualizarStatusOrdemUseCase {

    private final OrdemServicoRepository ordemServicoRepository;
    private final OrdemServicoMapper ordemServicoMapper;
    private final ApplicationEventPublisher eventPublisher;

    public AtualizarStatusOrdemUseCase(
            OrdemServicoRepository ordemServicoRepository,
            OrdemServicoMapper ordemServicoMapper,
            ApplicationEventPublisher eventPublisher) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.ordemServicoMapper = ordemServicoMapper;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public OrdemServicoResponseDTO executar(Long id, StatusOrdemServico novoStatus) {
        if (novoStatus == StatusOrdemServico.DIAGNOSTICO_CONCLUIDO) {
            throw new RegraNegocioException("Para concluir o diagnóstico, utilize o fluxo específico de conclusão");
        }
        OrdemServico ordemServico = ordemServicoRepository.consultarPorId(id)
                .orElseThrow(() -> new RegraNegocioException("Ordem de serviço não encontrada"));

        ordemServico.mudarStatus(novoStatus);
        OrdemServico salva = ordemServicoRepository.salvar(ordemServico);

        // Se o status for de notificação ou de aguardando aprovação, dispara o evento de notificação
        if (novoStatus == StatusOrdemServico.PENDENTE_APROVACAO_CLIENTE
                || novoStatus == StatusOrdemServico.PENDENTE_NOTIFICACAO_CLIENTE) {
            eventPublisher.publishEvent(new OrdemServicoAguardandoAprovacaoEvent(salva.getId()));

            // Se estava pendente de notificação, transita automaticamente para aguardando aprovação do cliente
            if (novoStatus == StatusOrdemServico.PENDENTE_NOTIFICACAO_CLIENTE) {
                salva.mudarStatus(StatusOrdemServico.PENDENTE_APROVACAO_CLIENTE);
                salva = ordemServicoRepository.salvar(salva);
            }
        }

        return ordemServicoMapper.toResponseDTO(salva);
    }
}
