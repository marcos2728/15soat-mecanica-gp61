package com.postech.mecanica.application.useCase.cliente;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.postech.mecanica.application.dto.ClienteVeiculoResponseDTO;
import com.postech.mecanica.application.mapper.ClienteMapper;
import com.postech.mecanica.domain.entity.Cliente;
import com.postech.mecanica.domain.repository.ClienteRepository;
import com.postech.mecanica.domain.vo.CpfCnpj;
import com.postech.mecanica.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class ConsultarClienteUseCaseTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ClienteMapper clienteMapper;

    @InjectMocks
    private ConsultarClienteUseCase consultarClienteUseCase;

    @Test
    @DisplayName("Deve consultar cliente por documento com sucesso")
    void deveConsultarClientePorDocumentoComSucesso() {
        String doc = "12345678901";
        Cliente cliente = new Cliente(1L, new CpfCnpj(doc), "Maria", "maria@email.com", "11988887777", "1992-05-10", List.of());
        ClienteVeiculoResponseDTO responseDTO = new ClienteVeiculoResponseDTO(1L, "Maria", doc, "maria@email.com", "11988887777", "1992-05-10", List.of());

        when(clienteRepository.consultarDocumentoCliente(doc)).thenReturn(Optional.of(cliente));
        when(clienteMapper.toDTO(cliente)).thenReturn(responseDTO);

        ClienteVeiculoResponseDTO resultado = consultarClienteUseCase.executar(doc);

        assertNotNull(resultado);
        assertEquals("Maria", resultado.nome());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando cliente não for encontrado por documento")
    void deveLancarExcecaoQuandoNaoEncontrado() {
        String doc = "11122233344";
        when(clienteRepository.consultarDocumentoCliente(doc)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> consultarClienteUseCase.executar(doc));
    }
}
