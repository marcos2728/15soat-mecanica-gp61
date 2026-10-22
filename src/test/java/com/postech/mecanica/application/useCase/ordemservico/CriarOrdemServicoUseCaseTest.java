package com.postech.mecanica.application.useCase.ordemservico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.postech.mecanica.application.dto.OrdemServicoRequestDTO;
import com.postech.mecanica.application.dto.OrdemServicoResponseDTO;
import com.postech.mecanica.application.mapper.OrdemServicoMapper;
import com.postech.mecanica.domain.entity.OrdemServico;
import com.postech.mecanica.domain.entity.Veiculo;
import com.postech.mecanica.domain.repository.OrdemServicoRepository;
import com.postech.mecanica.domain.repository.VeiculoRepository;
import com.postech.mecanica.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class CriarOrdemServicoUseCaseTest {

    @Mock
    private VeiculoRepository veiculoRepository;

    @Mock
    private OrdemServicoRepository ordemServicoRepository;

    @Mock
    private OrdemServicoMapper ordemServicoMapper;

    @InjectMocks
    private CriarOrdemServicoUseCase useCase;

    @Test
    @DisplayName("Deve criar ordem de serviço com sucesso")
    void deveCriarOrdemServicoComSucesso() {
        Long veiculoId = 10L;
        OrdemServicoRequestDTO request = new OrdemServicoRequestDTO(veiculoId);
        Veiculo veiculo = mock(Veiculo.class);
        OrdemServico salvo = mock(OrdemServico.class);
        OrdemServicoResponseDTO responseDTO = mock(OrdemServicoResponseDTO.class);

        when(veiculoRepository.consultarPorId(veiculoId)).thenReturn(Optional.of(veiculo));
        when(ordemServicoRepository.salvar(any(OrdemServico.class))).thenReturn(salvo);
        when(ordemServicoMapper.toResponseDTO(salvo)).thenReturn(responseDTO);

        OrdemServicoResponseDTO resultado = useCase.executar(request);

        assertNotNull(resultado);
        assertEquals(responseDTO, resultado);
        verify(ordemServicoRepository).salvar(any(OrdemServico.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando veículo não existir")
    void deveLancarExcecaoQuandoVeiculoNaoExistir() {
        Long veiculoId = 99L;
        OrdemServicoRequestDTO request = new OrdemServicoRequestDTO(veiculoId);

        when(veiculoRepository.consultarPorId(veiculoId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> useCase.executar(request));
    }
}
