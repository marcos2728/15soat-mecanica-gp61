package com.postech.mecanica.application.mapper;

import com.postech.mecanica.application.dto.ServicoDTO;
import com.postech.mecanica.infrastructure.persistence.entity.ServicoEntity;

public class ServicoMapper {

    public ServicoEntity toEntity(ServicoDTO servicoDTO) {
        ServicoEntity servico = new ServicoEntity();
        servico.setId(servicoDTO.getId());
        servico.setNomeServico(servicoDTO.getNomeServico());
        servico.setValorServico(servicoDTO.getValorServico());
        return servico;
    }

    public ServicoDTO toDTO(ServicoEntity servico) {
        ServicoDTO servicoDTO = new ServicoDTO();
        servicoDTO.setId(servico.getId());
        servicoDTO.setNomeServico(servico.getNomeServico());
        servicoDTO.setValorServico(servico.getValorServico());
        return servicoDTO;
    }

}
