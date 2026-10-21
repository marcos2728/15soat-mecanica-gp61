package com.postech.mecanica.application.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.postech.mecanica.application.dto.ItemEstoqueResponseDTO;
import com.postech.mecanica.application.dto.ItemServicoResponseDTO;
import com.postech.mecanica.application.dto.OrdemServicoResponseDTO;
import com.postech.mecanica.domain.entity.OrdemServico;
import com.postech.mecanica.infrastructure.persistence.entity.OrdemServicoEntity;

@Component
public class OrdemServicoMapper {

    public OrdemServicoResponseDTO toResponseDTO(OrdemServico ordemServico) {
        if (ordemServico == null) {
            return null;
        }

        List<ItemEstoqueResponseDTO> pecas = ordemServico.getPecas() != null
                ? ordemServico.getPecas().stream()
                        .map(item -> {
                            ItemEstoqueResponseDTO i = new ItemEstoqueResponseDTO();
                            i.setEstoqueId(item.getEstoqueId());
                            i.setNomePeca(item.getNomePeca());
                            i.setQuantidade(item.getQuantidade());
                            i.setValorUnitario(item.getValorUnitario());
                            i.setDisponivel(item.getDisponivel());
                            return i;
                        }).collect(Collectors.toList())
                : List.of();

        List<ItemServicoResponseDTO> servicos = ordemServico.getServicos() != null
                ? ordemServico.getServicos().stream()
                        .map(item -> {
                            ItemServicoResponseDTO s = new ItemServicoResponseDTO();
                            s.setServicoId(item.getServicoId());
                            s.setNomeServico(item.getNomeServico());
                            s.setValorAplicado(item.getValorAplicado());
                            return s;
                        }).collect(Collectors.toList())
                : List.of();

        String placa = null;
        if (ordemServico.getVeiculo() != null && ordemServico.getVeiculo().getPlaca() != null) {
            placa = ordemServico.getVeiculo().getPlaca().placa();
        }

        return new OrdemServicoResponseDTO(
                ordemServico.getId(),
                ordemServico.getVeiculo() != null ? ordemServico.getVeiculo().getId() : null,
                placa,
                ordemServico.getStatus(),
                ordemServico.getDescricaoProblema(),
                pecas,
                servicos,
                ordemServico.getValorTotal(),
                ordemServico.getDataHoraCriacao(),
                ordemServico.getDataHoraAtualizacao());
    }

    public OrdemServicoResponseDTO toResponseDTO(OrdemServicoEntity ordemServico) {
        if (ordemServico == null) {
            return null;
        }

        List<ItemEstoqueResponseDTO> pecas = ordemServico.getPecas() != null
                ? ordemServico.getPecas().stream()
                        .map(item -> {
                            ItemEstoqueResponseDTO i = new ItemEstoqueResponseDTO();
                            i.setEstoqueId(item.getEstoque().getId());
                            i.setNomePeca(item.getEstoque().getNomeItem());
                            i.setQuantidade(item.getQuantidade());
                            i.setValorUnitario(item.getValorUnitario());
                            i.setDisponivel(item.getDisponivel());
                            return i;
                        }).collect(Collectors.toList())
                : List.of();

        List<ItemServicoResponseDTO> servicos = ordemServico.getServicos() != null
                ? ordemServico.getServicos().stream()
                        .map(item -> {
                            ItemServicoResponseDTO s = new ItemServicoResponseDTO();
                            s.setServicoId(item.getServico().getId());
                            s.setNomeServico(item.getServico().getNomeServico());
                            s.setValorAplicado(item.getValorAplicado());
                            return s;
                        }).collect(Collectors.toList())
                : List.of();

        return new OrdemServicoResponseDTO(
                ordemServico.getId(),
                ordemServico.getVeiculo() != null ? ordemServico.getVeiculo().getId() : null,
                ordemServico.getVeiculo() != null ? ordemServico.getVeiculo().getPlaca() : null,
                ordemServico.getStatus(),
                ordemServico.getDescricaoProblema(),
                pecas,
                servicos,
                ordemServico.getValorTotal(),
                ordemServico.getDataHoraCriacao(),
                ordemServico.getDataHoraAtualizacao());
    }
}