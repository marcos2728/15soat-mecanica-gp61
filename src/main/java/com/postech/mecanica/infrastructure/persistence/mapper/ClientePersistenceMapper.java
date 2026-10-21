package com.postech.mecanica.infrastructure.persistence.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.postech.mecanica.domain.entity.Cliente;
import com.postech.mecanica.domain.entity.Veiculo;
import com.postech.mecanica.domain.vo.CpfCnpj;
import com.postech.mecanica.domain.vo.Placa;
import com.postech.mecanica.infrastructure.persistence.entity.ClienteEntity;
import com.postech.mecanica.infrastructure.persistence.entity.VeiculoEntity;

@Component
public class ClientePersistenceMapper {

    public ClienteEntity toEntity(Cliente domain) {
        if (domain == null) return null;
        ClienteEntity entity = new ClienteEntity();
        entity.setId(domain.getId());
        entity.setCpfCnpj(domain.getCpfCnpj() != null ? domain.getCpfCnpj().cpfCnpj() : null);
        entity.setNome(domain.getNome());
        entity.setEmail(domain.getEmail());
        entity.setTelefone(domain.getTelefone());
        entity.setDataDeNascimento(domain.getDataDeNascimento());

        if (domain.getVeiculos() != null) {
            List<VeiculoEntity> veiculoEntities = new ArrayList<>();
            for (Veiculo v : domain.getVeiculos()) {
                VeiculoEntity ve = new VeiculoEntity();
                ve.setId(v.getId());
                ve.setPlaca(v.getPlaca() != null ? v.getPlaca().placa() : null);
                ve.setModelo(v.getModelo());
                ve.setMarca(v.getMarca());
                ve.setAnoFabricacao(v.getAnoFabricacao());
                veiculoEntities.add(ve);
            }
            entity.setVeiculos(veiculoEntities);
        }
        return entity;
    } 

    public Cliente toDomain(ClienteEntity entity) {
        if (entity == null) return null;

        List<Veiculo> veiculos = new ArrayList<>();
        if (entity.getVeiculos() != null) {
            for (VeiculoEntity ve : entity.getVeiculos()) {
                veiculos.add(new Veiculo(
                    ve.getId(),
                    ve.getPlaca() != null ? new Placa(ve.getPlaca()) : null,
                    ve.getModelo(),
                    ve.getMarca(),
                    ve.getAnoFabricacao()
                ));
            }
        }

        return new Cliente(
            entity.getId(),
            new CpfCnpj(entity.getCpfCnpj()),
            entity.getNome(),
            entity.getEmail(),
            entity.getTelefone(),
            entity.getDataDeNascimento(),
            veiculos
        );
    }
}
