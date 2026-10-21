package com.postech.mecanica.application.useCase.ordemservico;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postech.mecanica.application.dto.DiagnosticoConcluidoRequestDTO;
import com.postech.mecanica.application.dto.ItemEstoqueRequestDTO;
import com.postech.mecanica.application.dto.ItemServicoRequestDTO;
import com.postech.mecanica.application.dto.OrdemServicoResponseDTO;
import com.postech.mecanica.application.mapper.OrdemServicoMapper;
import com.postech.mecanica.domain.entity.OrdemServico;
import com.postech.mecanica.domain.entity.OrdemServicoItemEstoque;
import com.postech.mecanica.domain.entity.OrdemServicoItemServico;
import com.postech.mecanica.domain.repository.EstoqueRepository;
import com.postech.mecanica.domain.repository.OrdemServicoRepository;
import com.postech.mecanica.domain.repository.ServicoRepository;
import com.postech.mecanica.exception.RegraNegocioException;
import com.postech.mecanica.exception.ResourceNotFoundException;
import com.postech.mecanica.domain.entity.Estoque;
import com.postech.mecanica.infrastructure.persistence.entity.ServicoEntity;

@Service
public class AtualizarDiagnosticoConlcuidoUseCase {

    private final OrdemServicoRepository ordemServicoRepository;
    private final OrdemServicoMapper ordemServicoMapper;
    private final EstoqueRepository estoqueRepository;
    private final ServicoRepository servicoRepository;

    public AtualizarDiagnosticoConlcuidoUseCase(
            OrdemServicoRepository ordemServicoRepository,
            OrdemServicoMapper ordemServicoMapper,
            EstoqueRepository estoqueRepository,
            ServicoRepository servicoRepository) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.ordemServicoMapper = ordemServicoMapper;
        this.estoqueRepository = estoqueRepository;
        this.servicoRepository = servicoRepository;
    }

    @Transactional
    public OrdemServicoResponseDTO executar(Long id, DiagnosticoConcluidoRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new RegraNegocioException("Dados do diagnóstico são obrigatórios");
        }

        OrdemServico ordemServico = ordemServicoRepository.consultarPorId(id)
                .orElseThrow(() -> new RegraNegocioException("Ordem de serviço não encontrada"));

        List<OrdemServicoItemEstoque> pecas = new ArrayList<>();
        if (requestDTO.pecas() != null) {
            for (ItemEstoqueRequestDTO item : requestDTO.pecas()) {
                Estoque estoque = estoqueRepository.consultarPorId(item.getEstoqueId())
                        .orElseThrow(() -> new ResourceNotFoundException("Peça não encontrada: " + item.getEstoqueId()));

                boolean disponivel = estoque.temEstoqueSuficiente(item.getQuantidade());
                if (disponivel) {
                    estoque.baixarEstoque(item.getQuantidade());
                    estoqueRepository.salvar(estoque);
                }

                pecas.add(new OrdemServicoItemEstoque(
                        estoque.getId(),
                        estoque.getNomeItem(),
                        item.getQuantidade(),
                        estoque.getValorItem(),
                        disponivel
                ));
            }
        }

        List<OrdemServicoItemServico> servicos = new ArrayList<>();
        if (requestDTO.servicos() != null) {
            for (ItemServicoRequestDTO item : requestDTO.servicos()) {
                ServicoEntity servico = servicoRepository.findById(item.getServicoId())
                        .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado: " + item.getServicoId()));

                servicos.add(new OrdemServicoItemServico(
                        servico.getId(),
                        servico.getNomeServico(),
                        servico.getValorServico()
                ));
            }
        }

        ordemServico.setDescricaoProblema(requestDTO.descricaoProblema());
        ordemServico.diagnosticoConcluido(pecas, servicos);

        return ordemServicoMapper.toResponseDTO(ordemServicoRepository.salvar(ordemServico));
    }

}
