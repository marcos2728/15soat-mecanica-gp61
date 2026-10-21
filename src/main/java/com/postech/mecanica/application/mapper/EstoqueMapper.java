package com.postech.mecanica.application.mapper;

import org.springframework.stereotype.Component;

import com.postech.mecanica.application.dto.EstoqueRequestDTO;
import com.postech.mecanica.application.dto.EstoqueResponseDTO;
import com.postech.mecanica.domain.entity.Estoque;
import com.postech.mecanica.infrastructure.persistence.entity.EstoqueEntity;

@Component
public class EstoqueMapper {

    public Estoque toDomain(EstoqueRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Estoque(
                null,
                dto.nomeItem(),
                dto.quantidadeItem(),
                dto.valorItem());
    }

    public Estoque toDomain(EstoqueResponseDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Estoque(
                dto.id(),
                dto.nomeItem(),
                dto.quantidadeItem(),
                dto.valorItem());
    }

    public EstoqueResponseDTO toResponseDTO(Estoque domain) {
        if (domain == null) {
            return null;
        }
        return new EstoqueResponseDTO(
                domain.getId(),
                domain.getNomeItem(),
                domain.getQuantidadeItem(),
                domain.getValorItem());
    }

    public EstoqueResponseDTO toDTO(EstoqueEntity entity) {
        if (entity == null) {
            return null;
        }
        return new EstoqueResponseDTO(
                entity.getId(),
                entity.getNomeItem(),
                entity.getQuantidadeItem(),
                entity.getValorItem());
    }

    public EstoqueEntity toEntity(EstoqueRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        EstoqueEntity entity = new EstoqueEntity();
        entity.setNomeItem(dto.nomeItem());
        entity.setQuantidadeItem(dto.quantidadeItem());
        entity.setValorItem(dto.valorItem());
        return entity;
    }

}
