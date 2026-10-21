package com.postech.mecanica.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;

import com.postech.mecanica.domain.entity.Estoque;
import com.postech.mecanica.infrastructure.persistence.entity.EstoqueEntity;

@Component
public class EstoquePersistenceMapper {

    public EstoqueEntity toEntity(Estoque domain) {
        if (domain == null) {
            return null;
        }

        EstoqueEntity entity = new EstoqueEntity();
        entity.setId(domain.getId());
        entity.setNomeItem(domain.getNomeItem());
        entity.setQuantidadeItem(domain.getQuantidadeItem());
        entity.setValorItem(domain.getValorItem());
        return entity;
    }

    public Estoque toDomain(EstoqueEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Estoque(
                entity.getId(),
                entity.getNomeItem(),
                entity.getQuantidadeItem(),
                entity.getValorItem());
    }
}
