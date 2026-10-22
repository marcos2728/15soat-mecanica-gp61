package com.postech.mecanica.application.useCase;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
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
class ServicoServiceTest {

    @Mock
    private ServicoRepository servicoRepository;

    @Mock
    private ServicoMapper servicoMapper;

    @InjectMocks
    private ServicoService servicoService;

    @Test
    @DisplayName("Deve salvar serviço com sucesso")
    void deveSalvarServico() {
        ServicoRequestDTO request = new ServicoRequestDTO("Pintura", 500.0);
        Servico servico = new Servico(1L, "Pintura", 500.0);
        ServicoResponseDTO response = new ServicoResponseDTO(1L, "Pintura", 500.0);

        when(servicoMapper.toDomain(request)).thenReturn(servico);
        when(servicoRepository.salvar(servico)).thenReturn(servico);
        when(servicoMapper.toResponseDTO(servico)).thenReturn(response);

        ServicoResponseDTO res = servicoService.salvar(request);
        assertNotNull(res);
        assertEquals("Pintura", res.nomeServico());
    }

    @Test
    @DisplayName("Deve listar todos os serviços")
    void deveListarTodos() {
        Servico servico = new Servico(1L, "Pintura", 500.0);
        ServicoResponseDTO response = new ServicoResponseDTO(1L, "Pintura", 500.0);

        when(servicoRepository.consultarTodos()).thenReturn(List.of(servico));
        when(servicoMapper.toResponseDTO(servico)).thenReturn(response);

        List<ServicoResponseDTO> list = servicoService.listarTodos();
        assertEquals(1, list.size());
    }

    @Test
    @DisplayName("Deve obter serviço por ID com sucesso e lançar exceção quando inexistente")
    void deveObterServicoPorId() {
        Long id = 1L;
        Servico servico = new Servico(id, "Pintura", 500.0);
        ServicoResponseDTO response = new ServicoResponseDTO(id, "Pintura", 500.0);

        when(servicoRepository.consultarPorId(id)).thenReturn(Optional.of(servico));
        when(servicoMapper.toResponseDTO(servico)).thenReturn(response);

        ServicoResponseDTO res = servicoService.ServicoPorId(id);
        assertNotNull(res);

        when(servicoRepository.consultarPorId(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> servicoService.ServicoPorId(99L));
    }

    @Test
    @DisplayName("Deve atualizar serviço com sucesso e lançar exceção quando inexistente")
    void deveAtualizarServico() {
        Long id = 1L;
        Servico servico = new Servico(id, "Pintura", 500.0);
        ServicoRequestDTO request = new ServicoRequestDTO("Pintura Geral", 700.0);
        ServicoResponseDTO response = new ServicoResponseDTO(id, "Pintura Geral", 700.0);

        when(servicoRepository.consultarPorId(id)).thenReturn(Optional.of(servico));
        when(servicoRepository.salvar(servico)).thenReturn(servico);
        when(servicoMapper.toResponseDTO(servico)).thenReturn(response);

        ServicoResponseDTO res = servicoService.atualizar(id, request);
        assertNotNull(res);
        assertEquals("Pintura Geral", res.nomeServico());

        when(servicoRepository.consultarPorId(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> servicoService.atualizar(99L, request));
    }

    @Test
    @DisplayName("Deve deletar serviço quando existir e lançar exceção quando não existir")
    void deveDeletarServico() {
        Long id = 1L;
        when(servicoRepository.existsById(id)).thenReturn(true);
        assertDoesNotThrow(() -> servicoService.deletar(id));
        verify(servicoRepository).deletarPorId(id);

        when(servicoRepository.existsById(99L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> servicoService.deletar(99L));
        verify(servicoRepository, never()).deletarPorId(99L);
    }
}
