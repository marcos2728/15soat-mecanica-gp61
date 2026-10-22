package com.postech.mecanica.application.useCase.ordemservico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.postech.mecanica.application.dto.OrdemServicoResponseDTO;
import com.postech.mecanica.application.mapper.OrdemServicoMapper;
import com.postech.mecanica.domain.entity.Cliente;
import com.postech.mecanica.domain.entity.OrdemServico;
import com.postech.mecanica.domain.entity.Veiculo;
import com.postech.mecanica.domain.repository.ClienteRepository;
import com.postech.mecanica.domain.repository.OrdemServicoRepository;
import com.postech.mecanica.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class ConsultarOrdemClienteUseCaseTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private OrdemServicoRepository ordemServicoRepository;

    @Mock
    private OrdemServicoMapper ordemServicoMapper;

    @InjectMocks
    private ConsultarOrdemClienteUseCase useCase;

    @Test
    @DisplayName("Deve consultar ordens de serviço por documento do cliente com sucesso")
    void deveConsultarPorCliente() {
        String doc = "12345678901";
        Cliente cliente = mock(Cliente.class);
        Veiculo veiculo = mock(Veiculo.class);
        OrdemServico os = mock(OrdemServico.class);
        OrdemServicoResponseDTO dto = mock(OrdemServicoResponseDTO.class);

        when(clienteRepository.consultarDocumentoCliente(doc)).thenReturn(Optional.of(cliente));
        when(cliente.getVeiculos()).thenReturn(List.of(veiculo));
        when(veiculo.getId()).thenReturn(10L);
        when(ordemServicoRepository.buscarPorVeiculoId(10L)).thenReturn(List.of(os));
        when(ordemServicoMapper.toResponseDTO(os)).thenReturn(dto);

        List<OrdemServicoResponseDTO> resultado = useCase.executar(doc);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando cliente não for encontrado")
    void deveLancarExcecaoQuandoClienteNaoEncontrado() {
        String doc = "99988877766";
        when(clienteRepository.consultarDocumentoCliente(doc)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> useCase.executar(doc));
    }
}
