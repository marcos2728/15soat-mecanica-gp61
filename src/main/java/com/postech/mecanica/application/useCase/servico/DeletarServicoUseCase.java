package com.postech.mecanica.application.useCase.servico;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postech.mecanica.domain.repository.ServicoRepository;
import com.postech.mecanica.exception.ResourceNotFoundException;

@Service
public class DeletarServicoUseCase {

    private final ServicoRepository servicoRepository;

    public DeletarServicoUseCase(ServicoRepository servicoRepository) {
        this.servicoRepository = servicoRepository;
    }

    @Transactional
    public void executar(Long id) {
        if (!servicoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Serviço não encontrado com o ID: " + id);
        }
        servicoRepository.deletarPorId(id);
    }
}
