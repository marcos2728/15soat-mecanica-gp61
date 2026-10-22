package com.postech.mecanica.application.useCase.veiculo;

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

import com.postech.mecanica.application.dto.VeiculoResponseDTO;
import com.postech.mecanica.application.mapper.VeiculoMapper;
import com.postech.mecanica.domain.entity.Cliente;
import com.postech.mecanica.domain.entity.Veiculo;
import com.postech.mecanica.domain.repository.ClienteRepository;
import com.postech.mecanica.domain.vo.CpfCnpj;
import com.postech.mecanica.domain.vo.Placa;
import com.postech.mecanica.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class ConsultarVeiculosClienteUseCaseTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private VeiculoMapper veiculoMapper;

    @InjectMocks
    private ConsultarVeiculosClienteUseCase consultarVeiculosClienteUseCase;

    @Test
    @DisplayName("Deve consultar veículos de um cliente com sucesso")
    void deveConsultarVeiculosClienteComSucesso() {
        String doc = "12345678901";
        Veiculo v1 = new Veiculo(1L, new Placa("ABC1D23"), "Gol", "VW", 2018);
        Cliente cliente = new Cliente(10L, new CpfCnpj(doc), "Carlos", "carlos@email.com", "11988887777", "1985-04-12", List.of(v1));
        VeiculoResponseDTO dto = new VeiculoResponseDTO(1L, "ABC1D23", "Gol", "VW", 2018);

        when(clienteRepository.consultarDocumentoCliente(doc)).thenReturn(Optional.of(cliente));
        when(veiculoMapper.toDTO(v1)).thenReturn(dto);

        List<VeiculoResponseDTO> resultado = consultarVeiculosClienteUseCase.executar(doc);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("ABC1D23", resultado.get(0).placa());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando cliente não for encontrado")
    void deveLancarExcecaoQuandoClienteNaoEncontrado() {
        String doc = "11122233344";
        when(clienteRepository.consultarDocumentoCliente(doc)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> consultarVeiculosClienteUseCase.executar(doc));
    }
}
