package com.postech.mecanica.infrastructure.persistence.mapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.postech.mecanica.domain.entity.OrdemServico;
import com.postech.mecanica.domain.entity.OrdemServicoItemEstoque;
import com.postech.mecanica.domain.entity.OrdemServicoItemServico;
import com.postech.mecanica.domain.entity.Veiculo;
import com.postech.mecanica.domain.enums.StatusOrdemServico;
import com.postech.mecanica.infrastructure.persistence.entity.EstoqueEntity;
import com.postech.mecanica.infrastructure.persistence.entity.OrdemServicoEntity;
import com.postech.mecanica.infrastructure.persistence.entity.OrdemServicoItemEstoqueEntity;
import com.postech.mecanica.infrastructure.persistence.entity.OrdemServicoItemServicoEntity;
import com.postech.mecanica.infrastructure.persistence.entity.ServicoEntity;

@Component
public class OrdemServicoPersistenceMapper {

    private final VeiculoPersistenceMapper veiculoPersistenceMapper;

    public OrdemServicoPersistenceMapper(VeiculoPersistenceMapper veiculoPersistenceMapper) {
        this.veiculoPersistenceMapper = veiculoPersistenceMapper;
    }

    public OrdemServicoEntity toEntity(OrdemServico domain) {
        if (domain == null) {
            return null;
        }

        OrdemServicoEntity entity = new OrdemServicoEntity();
        entity.setId(domain.getId());
        entity.setVeiculo(veiculoPersistenceMapper.toEntity(domain.getVeiculo()));
        entity.setStatus(domain.getStatus() != null ? domain.getStatus() : StatusOrdemServico.CRIADA);
        entity.setDescricaoProblema(domain.getDescricaoProblema());
        entity.setValorTotal(domain.getValorTotal());
        entity.setDataHoraCriacao(domain.getDataHoraCriacao() != null ? domain.getDataHoraCriacao() : LocalDateTime.now());
        entity.setDataHoraAtualizacao(domain.getDataHoraAtualizacao());

        if (domain.getPecas() != null) {
            List<OrdemServicoItemEstoqueEntity> pecasEntities = new ArrayList<>();
            for (OrdemServicoItemEstoque pecaDomain : domain.getPecas()) {
                OrdemServicoItemEstoqueEntity pecaEntity = new OrdemServicoItemEstoqueEntity();
                pecaEntity.setId(pecaDomain.getId());
                pecaEntity.setOrdemServico(entity);

                if (pecaDomain.getEstoqueId() != null) {
                    EstoqueEntity estoqueEntity = new EstoqueEntity();
                    estoqueEntity.setId(pecaDomain.getEstoqueId());
                    estoqueEntity.setNomeItem(pecaDomain.getNomePeca());
                    estoqueEntity.setValorItem(pecaDomain.getValorUnitario());
                    pecaEntity.setEstoque(estoqueEntity);
                }

                pecaEntity.setQuantidade(pecaDomain.getQuantidade());
                pecaEntity.setValorUnitario(pecaDomain.getValorUnitario());
                pecaEntity.setDisponivel(pecaDomain.getDisponivel());
                pecasEntities.add(pecaEntity);
            }
            entity.setPecas(pecasEntities);
        }

        if (domain.getServicos() != null) {
            List<OrdemServicoItemServicoEntity> servicosEntities = new ArrayList<>();
            for (OrdemServicoItemServico servicoDomain : domain.getServicos()) {
                OrdemServicoItemServicoEntity servicoEntity = new OrdemServicoItemServicoEntity();
                servicoEntity.setId(servicoDomain.getId());
                servicoEntity.setOrdemServico(entity);

                if (servicoDomain.getServicoId() != null) {
                    ServicoEntity servEntity = new ServicoEntity();
                    servEntity.setId(servicoDomain.getServicoId());
                    servEntity.setNomeServico(servicoDomain.getNomeServico());
                    servEntity.setValorServico(servicoDomain.getValorAplicado());
                    servicoEntity.setServico(servEntity);
                }

                servicoEntity.setValorAplicado(servicoDomain.getValorAplicado());
                servicosEntities.add(servicoEntity);
            }
            entity.setServicos(servicosEntities);
        }

        return entity;
    }

    public OrdemServico toDomain(OrdemServicoEntity entity) {
        if (entity == null) {
            return null;
        }

        Veiculo veiculo = veiculoPersistenceMapper.toDomain(entity.getVeiculo());

        List<OrdemServicoItemEstoque> pecas = new ArrayList<>();
        if (entity.getPecas() != null) {
            for (OrdemServicoItemEstoqueEntity p : entity.getPecas()) {
                Long estoqueId = p.getEstoque() != null ? p.getEstoque().getId() : null;
                String nomePeca = p.getEstoque() != null ? p.getEstoque().getNomeItem() : null;
                if (estoqueId != null && p.getQuantidade() != null && p.getQuantidade() > 0) {
                    pecas.add(new OrdemServicoItemEstoque(
                            p.getId(),
                            estoqueId,
                            nomePeca,
                            p.getQuantidade(),
                            p.getValorUnitario(),
                            p.getDisponivel()
                    ));
                }
            }
        }

        List<OrdemServicoItemServico> servicos = new ArrayList<>();
        if (entity.getServicos() != null) {
            for (OrdemServicoItemServicoEntity s : entity.getServicos()) {
                Long servicoId = s.getServico() != null ? s.getServico().getId() : null;
                String nomeServico = s.getServico() != null ? s.getServico().getNomeServico() : null;
                if (servicoId != null && s.getValorAplicado() != null && s.getValorAplicado() >= 0) {
                    servicos.add(new OrdemServicoItemServico(
                            s.getId(),
                            servicoId,
                            nomeServico,
                            s.getValorAplicado()
                    ));
                }
            }
        }

        return new OrdemServico(
                entity.getId(),
                veiculo,
                entity.getStatus(),
                entity.getDescricaoProblema(),
                pecas,
                servicos,
                entity.getValorTotal(),
                entity.getDataHoraCriacao(),
                entity.getDataHoraAtualizacao()
        );
    }
}
