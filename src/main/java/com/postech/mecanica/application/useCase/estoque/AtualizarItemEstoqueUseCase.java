package com.postech.mecanica.application.useCase.estoque;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postech.mecanica.application.dto.EstoqueRequestDTO;
import com.postech.mecanica.application.dto.EstoqueResponseDTO;
import com.postech.mecanica.domain.entity.Estoque;
import com.postech.mecanica.domain.event.EstoqueReabastecidoEvent;
import com.postech.mecanica.domain.repository.EstoqueRepository;
import com.postech.mecanica.exception.ResourceNotFoundException;

@Service
public class AtualizarItemEstoqueUseCase {

    private final EstoqueRepository estoqueRepository;
    private final ApplicationEventPublisher eventPublisher;

    public AtualizarItemEstoqueUseCase(
            EstoqueRepository estoqueRepository,
            ApplicationEventPublisher eventPublisher) {
        this.estoqueRepository = estoqueRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public EstoqueResponseDTO executar(Long idEstoque, EstoqueRequestDTO estoqueRequestDTO) {
        Estoque itemEstoque = estoqueRepository.consultarPorId(idEstoque)
                .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado com o ID: " + idEstoque));

        itemEstoque.atualizarDados(
                estoqueRequestDTO.nomeItem(),
                estoqueRequestDTO.quantidadeItem(),
                estoqueRequestDTO.valorItem()
        );

        estoqueRepository.salvar(itemEstoque);

        // Publica o evento de reabastecimento de estoque de forma síncrona
        EstoqueReabastecidoEvent evento = new EstoqueReabastecidoEvent(idEstoque);
        eventPublisher.publishEvent(evento);

        List<Long> ordensLiberadas = evento.getOrdensServicoLiberadas();

        // Recupera o saldo consolidado final após a reconciliação executada pelo listener
        Estoque estoqueFinal = estoqueRepository.consultarPorId(idEstoque)
                .orElse(itemEstoque);

        return new EstoqueResponseDTO(
                estoqueFinal.getId(),
                estoqueFinal.getNomeItem(),
                estoqueFinal.getQuantidadeItem(),
                estoqueFinal.getValorItem(),
                ordensLiberadas
        );
    }
}



