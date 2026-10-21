package com.postech.mecanica.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;

import com.postech.mecanica.domain.entity.Veiculo;
import com.postech.mecanica.domain.vo.Placa;
import com.postech.mecanica.infrastructure.persistence.entity.VeiculoEntity;

@Component
public class VeiculoPersistenceMapper {

    public VeiculoEntity toEntity(Veiculo domain) {
        if (domain == null) {
            return null;
        }

        VeiculoEntity entity = new VeiculoEntity();
        entity.setId(domain.getId());
        entity.setPlaca(domain.getPlaca() != null ? domain.getPlaca().placa() : null);
        entity.setModelo(domain.getModelo());
        entity.setMarca(domain.getMarca());
        entity.setAnoFabricacao(domain.getAnoFabricacao());
        return entity;
    }

    public Veiculo toDomain(VeiculoEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Veiculo(
                entity.getId(),
                entity.getPlaca() != null ? new Placa(entity.getPlaca()) : null,
                entity.getModelo(),
                entity.getMarca(),
                entity.getAnoFabricacao());
    }
}
