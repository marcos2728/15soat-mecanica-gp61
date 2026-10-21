package com.postech.mecanica.application.useCase.ordemservico;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postech.mecanica.application.dto.OrdemServicoResponseDTO;
import com.postech.mecanica.application.mapper.OrdemServicoMapper;
import com.postech.mecanica.domain.entity.OrdemServico;
import com.postech.mecanica.domain.enums.StatusOrdemServico;
import com.postech.mecanica.domain.repository.OrdemServicoRepository;
import com.postech.mecanica.exception.RegraNegocioException;
import com.postech.mecanica.exception.ResourceNotFoundException;

@Service
public class AprovarOrdemServicoUseCase {

    private static final Logger log = LoggerFactory.getLogger(AprovarOrdemServicoUseCase.class);

    private final OrdemServicoRepository ordemServicoRepository;
    private final OrdemServicoMapper ordemServicoMapper;

    public AprovarOrdemServicoUseCase(
            OrdemServicoRepository ordemServicoRepository,
            OrdemServicoMapper ordemServicoMapper) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.ordemServicoMapper = ordemServicoMapper;
    }

    @Transactional
    public OrdemServicoResponseDTO executar(Long id) {
        OrdemServico ordemServico = ordemServicoRepository.consultarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ordem de serviço não encontrada com ID: " + id));

        // Idempotência: caso já esteja aprovada
        if (ordemServico.getStatus() == StatusOrdemServico.APROVADA) {
            log.info("Ordem de serviço #{} já está aprovada.", id);
            return ordemServicoMapper.toResponseDTO(ordemServico);
        }

        // Validação de regras de negócio para transição
        if (ordemServico.getStatus() != StatusOrdemServico.PENDENTE_APROVACAO_CLIENTE
                && ordemServico.getStatus() != StatusOrdemServico.PENDENTE_NOTIFICACAO_CLIENTE) {
            throw new RegraNegocioException(
                    String.format("A Ordem de Serviço #%d não pode ser aprovada no status atual: %s. " +
                            "Apenas ordens aguardando aprovação podem ser aprovadas.", id, ordemServico.getStatus()));
        }

        ordemServico.mudarStatus(StatusOrdemServico.APROVADA);
        OrdemServico salva = ordemServicoRepository.salvar(ordemServico);
        log.info("Ordem de serviço #{} aprovada com sucesso pelo cliente.", id);

        return ordemServicoMapper.toResponseDTO(salva);
    }
}
