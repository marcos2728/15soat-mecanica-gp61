package com.postech.mecanica.application.useCase.veiculo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.postech.mecanica.application.dto.VeiculoRequestDTO;
import com.postech.mecanica.application.dto.VeiculoResponseDTO;
import com.postech.mecanica.application.mapper.VeiculoMapper;
import com.postech.mecanica.domain.entity.Cliente;
import com.postech.mecanica.domain.entity.Veiculo;
import com.postech.mecanica.domain.repository.ClienteRepository;
import com.postech.mecanica.domain.repository.VeiculoRepository;
import com.postech.mecanica.domain.vo.CpfCnpj;
import com.postech.mecanica.domain.vo.Placa;
import com.postech.mecanica.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class CadastrarVeiculoUseCaseTest {

    @Mock
    private VeiculoMapper veiculoMapper;

    @Mock
    private VeiculoRepository veiculoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private CadastrarVeiculoUseCase cadastrarVeiculoUseCase;

    @Test
    @DisplayName("Deve cadastrar veículo associado ao cliente com sucesso")
    void deveCadastrarVeiculoComSucesso() {
        Long idCliente = 1L;
        VeiculoRequestDTO dto = new VeiculoRequestDTO(null, "ABC1D23", "Civic", "Honda", 2022);
        Cliente cliente = new Cliente(idCliente, new CpfCnpj("12345678901"), "Carlos", "carlos@email.com", "11988887777", "1985-04-12", new ArrayList<>());
        Veiculo veiculo = new Veiculo(10L, new Placa("ABC1D23"), "Civic", "Honda", 2022);
        VeiculoResponseDTO responseDTO = new VeiculoResponseDTO(10L, "ABC1D23", "Civic", "Honda", 2022);

        when(clienteRepository.consultarPorId(idCliente)).thenReturn(Optional.of(cliente));
        when(veiculoMapper.toDomain(dto)).thenReturn(veiculo);
        when(veiculoRepository.salvar(veiculo)).thenReturn(veiculo);
        when(veiculoMapper.toDTO(veiculo)).thenReturn(responseDTO);

        VeiculoResponseDTO resultado = cadastrarVeiculoUseCase.executar(idCliente, dto);

        assertNotNull(resultado);
        assertEquals("ABC1D23", resultado.placa());
        verify(clienteRepository).salvar(cliente);
        verify(veiculoRepository).salvar(veiculo);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando cliente não for encontrado")
    void deveLancarExcecaoQuandoClienteNaoEncontrado() {
        Long idCliente = 99L;
        VeiculoRequestDTO dto = new VeiculoRequestDTO(null, "ABC1D23", "Civic", "Honda", 2022);
        when(clienteRepository.consultarPorId(idCliente)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cadastrarVeiculoUseCase.executar(idCliente, dto));
        verify(veiculoRepository, never()).salvar(any());
    }
}
