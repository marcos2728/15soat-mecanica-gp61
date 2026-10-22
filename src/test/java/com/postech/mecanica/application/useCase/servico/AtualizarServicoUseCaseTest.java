package com.postech.mecanica.application.useCase.servico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.postech.mecanica.application.dto.ServicoRequestDTO;
import com.postech.mecanica.application.dto.ServicoResponseDTO;
import com.postech.mecanica.application.mapper.ServicoMapper;
import com.postech.mecanica.domain.entity.Servico;
import com.postech.mecanica.domain.repository.ServicoRepository;
import com.postech.mecanica.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class AtualizarServicoUseCaseTest {

    @Mock
    private ServicoRepository servicoRepository;

    @Mock
    private ServicoMapper servicoMapper;

    @InjectMocks
    private AtualizarServicoUseCase atualizarServicoUseCase;

    @Test
    @DisplayName("Deve atualizar serviço existente com sucesso")
    void deveAtualizarServicoComSucesso() {
        Long id = 1L;
        Servico servico = new Servico(id, "Troca de Óleo", 150.0);
        ServicoRequestDTO request = new ServicoRequestDTO("Troca de Óleo e Filtro", 180.0);
        ServicoResponseDTO responseDTO = new ServicoResponseDTO(id, "Troca de Óleo e Filtro", 180.0);

        when(servicoRepository.consultarPorId(id)).thenReturn(Optional.of(servico));
        when(servicoRepository.salvar(servico)).thenReturn(servico);
        when(servicoMapper.toResponseDTO(servico)).thenReturn(responseDTO);

        ServicoResponseDTO resultado = atualizarServicoUseCase.executar(id, request);

        assertNotNull(resultado);
        assertEquals("Troca de Óleo e Filtro", resultado.nomeServico());
        verify(servicoRepository).salvar(servico);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando tentar atualizar serviço inexistente")
    void deveLancarExcecaoQuandoNaoEncontrado() {
        Long id = 99L;
        ServicoRequestDTO request = new ServicoRequestDTO("Troca", 100.0);
        when(servicoRepository.consultarPorId(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> atualizarServicoUseCase.executar(id, request));
        verify(servicoRepository, never()).salvar(any());
    }
}
