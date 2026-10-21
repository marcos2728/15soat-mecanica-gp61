package com.postech.mecanica.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;

import com.postech.mecanica.domain.entity.Servico;
import com.postech.mecanica.infrastructure.persistence.entity.ServicoEntity;

@Component
public class ServicoPersistenceMapper {

    public ServicoEntity toEntity(Servico domain) {
        if (domain == null) {
            return null;
        }

        ServicoEntity entity = new ServicoEntity();
        entity.setId(domain.getId());
        entity.setNomeServico(domain.getNomeServico());
        entity.setValorServico(domain.getValorServico());
        return entity;
    }

    public Servico toDomain(ServicoEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Servico(
                entity.getId(),
                entity.getNomeServico(),
                entity.getValorServico());
    }
}
