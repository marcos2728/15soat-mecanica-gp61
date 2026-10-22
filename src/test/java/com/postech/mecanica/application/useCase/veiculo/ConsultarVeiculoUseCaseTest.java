package com.postech.mecanica.application.useCase.veiculo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.postech.mecanica.application.dto.VeiculoResponseDTO;
import com.postech.mecanica.application.mapper.VeiculoMapper;
import com.postech.mecanica.domain.entity.Veiculo;
import com.postech.mecanica.domain.repository.VeiculoRepository;
import com.postech.mecanica.domain.vo.Placa;
import com.postech.mecanica.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class ConsultarVeiculoUseCaseTest {

    @Mock
    private VeiculoRepository veiculoRepository;

    @Mock
    private VeiculoMapper veiculoMapper;

    @InjectMocks
    private ConsultarVeiculoUseCase consultarVeiculoUseCase;

    @Test
    @DisplayName("Deve consultar veículo por ID com sucesso")
    void deveConsultarVeiculoPorIdComSucesso() {
        Long id = 1L;
        Veiculo veiculo = new Veiculo(id, new Placa("ABC1D23"), "Gol", "VW", 2018);
        VeiculoResponseDTO responseDTO = new VeiculoResponseDTO(id, "ABC1D23", "Gol", "VW", 2018);

        when(veiculoRepository.consultarPorId(id)).thenReturn(Optional.of(veiculo));
        when(veiculoMapper.toDTO(veiculo)).thenReturn(responseDTO);

        VeiculoResponseDTO resultado = consultarVeiculoUseCase.executar(id);

        assertNotNull(resultado);
        assertEquals(id, resultado.id());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando veículo não existir")
    void deveLancarExcecaoQuandoVeiculoNaoExistir() {
        Long id = 99L;
        when(veiculoRepository.consultarPorId(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> consultarVeiculoUseCase.executar(id));
    }
}
