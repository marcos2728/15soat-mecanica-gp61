package com.postech.mecanica.application.useCase.cliente;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.postech.mecanica.application.dto.ClienteVeiculoRequestDTO;
import com.postech.mecanica.application.dto.ClienteVeiculoResponseDTO;
import com.postech.mecanica.application.dto.VeiculoRequestDTO;
import com.postech.mecanica.application.mapper.ClienteMapper;
import com.postech.mecanica.application.mapper.VeiculoMapper;
import com.postech.mecanica.domain.entity.Cliente;
import com.postech.mecanica.domain.entity.Veiculo;
import com.postech.mecanica.domain.repository.ClienteRepository;
import com.postech.mecanica.domain.repository.VeiculoRepository;
import com.postech.mecanica.domain.vo.CpfCnpj;
import com.postech.mecanica.domain.vo.Placa;

@ExtendWith(MockitoExtension.class)
class CadastrarClienteUseCaseTest {

    @Mock
    private VeiculoMapper veiculoMapper;

    @Mock
    private VeiculoRepository veiculoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ClienteMapper clienteMapper;

    @InjectMocks
    private CadastrarClienteUseCase cadastrarClienteUseCase;

    @Test
    @DisplayName("Deve cadastrar cliente e veículo com sucesso")
    void deveCadastrarClienteComSucesso() {
        VeiculoRequestDTO veiculoDTO = new VeiculoRequestDTO(null, "ABC1D23", "Corolla", "Toyota", 2021);
        ClienteVeiculoRequestDTO request = new ClienteVeiculoRequestDTO("12345678901", "João", "1990-01-01", "joao@email.com", "11999998888", veiculoDTO);

        Veiculo veiculo = new Veiculo(1L, new Placa("ABC1D23"), "Corolla", "Toyota", 2021);
        Cliente cliente = new Cliente(1L, new CpfCnpj("12345678901"), "João", "joao@email.com", "11999998888", "1990-01-01", List.of(veiculo));
        ClienteVeiculoResponseDTO expectedResponse = new ClienteVeiculoResponseDTO(1L, "12345678901", "João", "1990-01-01", "joao@email.com", "11999998888", List.of());

        when(veiculoMapper.toDomain(veiculoDTO)).thenReturn(veiculo);
        when(veiculoRepository.salvar(veiculo)).thenReturn(veiculo);
        when(clienteMapper.toDomain(request, veiculo)).thenReturn(cliente);
        when(clienteRepository.salvar(cliente)).thenReturn(cliente);
        when(clienteMapper.toDTO(cliente, veiculo)).thenReturn(expectedResponse);

        ClienteVeiculoResponseDTO response = cadastrarClienteUseCase.executar(request);

        assertNotNull(response);
        assertEquals(expectedResponse, response);
        verify(veiculoRepository).salvar(veiculo);
        verify(clienteRepository).salvar(cliente);
    }
}
