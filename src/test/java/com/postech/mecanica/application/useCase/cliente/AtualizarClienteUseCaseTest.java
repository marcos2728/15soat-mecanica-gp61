package com.postech.mecanica.application.useCase.cliente;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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

import com.postech.mecanica.application.dto.ClienteRequestDTO;
import com.postech.mecanica.application.dto.ClienteResponseDTO;
import com.postech.mecanica.application.mapper.ClienteMapper;
import com.postech.mecanica.domain.entity.Cliente;
import com.postech.mecanica.domain.repository.ClienteRepository;
import com.postech.mecanica.domain.vo.CpfCnpj;
import com.postech.mecanica.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class AtualizarClienteUseCaseTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ClienteMapper clienteMapper;

    @InjectMocks
    private AtualizarClienteUseCase atualizarClienteUseCase;

    @Test
    @DisplayName("Deve atualizar cliente existente com sucesso")
    void deveAtualizarClienteComSucesso() {
        Long id = 1L;
        Cliente cliente = new Cliente(id, new CpfCnpj("12345678901"), "João Antigo", "antigo@email.com", "11988887777", "1990-01-01", List.of());
        ClienteRequestDTO request = new ClienteRequestDTO("João Novo", "1990-02-02", "novo@email.com", "11999998888");
        ClienteResponseDTO responseDTO = new ClienteResponseDTO(id, "João Novo", "12345678901", "novo@email.com", "11999998888", "1990-02-02");

        when(clienteRepository.consultarPorId(id)).thenReturn(Optional.of(cliente));
        when(clienteRepository.salvar(any(Cliente.class))).thenReturn(cliente);
        when(clienteMapper.toClienteResponseDTO(cliente)).thenReturn(responseDTO);

        ClienteResponseDTO resultado = atualizarClienteUseCase.executar(id, request);

        assertNotNull(resultado);
        assertEquals("João Novo", resultado.nome());
        verify(clienteRepository).salvar(cliente);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando cliente não for encontrado")
    void deveLancarExcecaoQuandoClienteNaoEncontrado() {
        Long id = 99L;
        ClienteRequestDTO request = new ClienteRequestDTO("João", "1990-01-01", "joao@email.com", "11999998888");
        when(clienteRepository.consultarPorId(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> atualizarClienteUseCase.executar(id, request));
        verify(clienteRepository, never()).salvar(any());
    }
}
