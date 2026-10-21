package com.postech.mecanica.application.mapper;

import org.springframework.stereotype.Component;

import com.postech.mecanica.application.dto.VeiculoRequestDTO;
import com.postech.mecanica.application.dto.VeiculoResponseDTO;
import com.postech.mecanica.domain.entity.Veiculo;
import com.postech.mecanica.domain.vo.Placa;

@Component
public class VeiculoMapper {

    public Veiculo toDomain(VeiculoRequestDTO dto) {

        return new Veiculo(
                null,
                new Placa(dto.placa()),
                dto.modelo(),
                dto.marca(),
                dto.anoFabricacao());
    }

    public VeiculoResponseDTO toDTO(Veiculo veiculo) {

        return new VeiculoResponseDTO(
                veiculo.getId(),
                veiculo.getPlaca().placa(),
                veiculo.getModelo(),
                veiculo.getMarca(),
                veiculo.getAnoFabricacao());
    }

}