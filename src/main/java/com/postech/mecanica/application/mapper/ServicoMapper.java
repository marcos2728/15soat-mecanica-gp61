package com.postech.mecanica.application.mapper;

import org.springframework.stereotype.Component;

import com.postech.mecanica.application.dto.ServicoRequestDTO;
import com.postech.mecanica.application.dto.ServicoResponseDTO;
import com.postech.mecanica.domain.entity.Servico;
import com.postech.mecanica.infrastructure.persistence.entity.ServicoEntity;

@Component
public class ServicoMapper {

    public Servico toDomain(ServicoRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Servico(
                null,
                dto.nomeServico(),
                dto.valorServico());
    }

    public Servico toDomain(ServicoResponseDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Servico(
                dto.id(),
                dto.nomeServico(),
                dto.valorServico());
    }

    public ServicoResponseDTO toResponseDTO(Servico domain) {
        if (domain == null) {
            return null;
        }
        return new ServicoResponseDTO(
                domain.getId(),
                domain.getNomeServico(),
                domain.getValorServico());
    }

    public ServicoResponseDTO toDTO(ServicoEntity entity) {
        if (entity == null) {
            return null;
        }
        return new ServicoResponseDTO(
                entity.getId(),
                entity.getNomeServico(),
                entity.getValorServico());
    }

    public ServicoEntity toEntity(ServicoRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        ServicoEntity entity = new ServicoEntity();
        entity.setNomeServico(dto.nomeServico());
        entity.setValorServico(dto.valorServico());
        return entity;
    }

}
