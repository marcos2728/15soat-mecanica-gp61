package com.postech.mecanica.application.useCase.veiculo;

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

import com.postech.mecanica.application.dto.VeiculoRequestDTO;
import com.postech.mecanica.application.dto.VeiculoResponseDTO;
import com.postech.mecanica.application.mapper.VeiculoMapper;
import com.postech.mecanica.domain.entity.Veiculo;
import com.postech.mecanica.domain.repository.VeiculoRepository;
import com.postech.mecanica.domain.vo.Placa;
import com.postech.mecanica.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class AtualizarVeiculoUseCaseTest {

    @Mock
    private VeiculoRepository veiculoRepository;

    @Mock
    private VeiculoMapper veiculoMapper;

    @InjectMocks
    private AtualizarVeiculoUseCase atualizarVeiculoUseCase;

    @Test
    @DisplayName("Deve atualizar veículo existente com sucesso")
    void deveAtualizarVeiculoComSucesso() {
        Long id = 1L;
        Veiculo veiculo = new Veiculo(id, new Placa("ABC1D23"), "Gol", "VW", 2018);
        VeiculoRequestDTO request = new VeiculoRequestDTO(id, "ABC1D23", "Polo", "Volkswagen", 2020);
        VeiculoResponseDTO responseDTO = new VeiculoResponseDTO(id, "ABC1D23", "Polo", "Volkswagen", 2020);

        when(veiculoRepository.consultarPorId(id)).thenReturn(Optional.of(veiculo));
        when(veiculoRepository.salvar(veiculo)).thenReturn(veiculo);
        when(veiculoMapper.toDTO(veiculo)).thenReturn(responseDTO);

        VeiculoResponseDTO resultado = atualizarVeiculoUseCase.executar(id, request);

        assertNotNull(resultado);
        assertEquals("Polo", resultado.modelo());
        verify(veiculoRepository).salvar(veiculo);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando veículo não existir")
    void deveLancarExcecaoQuandoVeiculoNaoExistir() {
        Long id = 99L;
        VeiculoRequestDTO request = new VeiculoRequestDTO(id, "ABC1D23", "Polo", "Volkswagen", 2020);
        when(veiculoRepository.consultarPorId(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> atualizarVeiculoUseCase.executar(id, request));
        verify(veiculoRepository, never()).salvar(any());
    }
}
