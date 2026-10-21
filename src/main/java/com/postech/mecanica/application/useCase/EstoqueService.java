package com.postech.mecanica.application.useCase;

import java.util.List;

import org.springframework.stereotype.Service;

import com.postech.mecanica.application.dto.EstoqueDTO;
import com.postech.mecanica.application.mapper.EstoqueMapper;
import com.postech.mecanica.application.useCase.ordemservico.ReconciliarPecasPendentesUseCase;
import com.postech.mecanica.domain.repository.EstoqueRepository;
import com.postech.mecanica.exception.ResourceNotFoundException;
import com.postech.mecanica.infrastructure.persistence.entity.EstoqueEntity;

@Service
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;
    private final EstoqueMapper estoqueMapper;
    private final ReconciliarPecasPendentesUseCase reconciliarPecasPendentesUseCase;

    public EstoqueService(EstoqueRepository estoqueRepository,
            EstoqueMapper estoqueMapper,
            ReconciliarPecasPendentesUseCase reconciliarPecasPendentesUseCase) {
        this.estoqueRepository = estoqueRepository;
        this.estoqueMapper = estoqueMapper;
        this.reconciliarPecasPendentesUseCase = reconciliarPecasPendentesUseCase;
    }

    public List<EstoqueDTO> listarTodos() {
        return estoqueRepository.findAll().stream()
                .map(estoqueMapper::toDTO)
                .toList();
    }

    public EstoqueDTO consultarItemId(Long id) {
        return estoqueRepository.findById(id)
                .map(estoqueMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado com o ID: " + id));
    }

    public void atualizar(Long id, EstoqueDTO estoqueDTO) {
        EstoqueEntity itemEstoque = estoqueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado com o ID: " + id));

        itemEstoque.setNomeItem(estoqueDTO.nomeItem());
        itemEstoque.setValorItem(estoqueDTO.valorItem());
        itemEstoque.setQuantidadeItem(estoqueDTO.quantidadeItem());

        // reconcilia ordens de serviço pendentes ANTES de salvar, já que isso
        // pode reduzir a quantidade de novo (reservando peça pras OS pendentes)
        reconciliarPecasPendentesUseCase.executar(itemEstoque);

        estoqueRepository.save(itemEstoque);
    }

    public void deletar(Long id) {
        if (!estoqueRepository.existsById(id)) {
            throw new ResourceNotFoundException("Item não encontrado com o ID: " + id);
        }
        estoqueRepository.deleteById(id);
    }
}