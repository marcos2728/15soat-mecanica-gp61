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

import com.postech.mecanica.application.dto.TempoPorFaseResponseDTO;
import com.postech.mecanica.application.mapper.OrdemServicoStatusHistoricoMapper;
import com.postech.mecanica.domain.entity.OrdemServico;
import com.postech.mecanica.domain.enums.StatusOrdemServico;
import com.postech.mecanica.domain.repository.OrdemServicoRepository;
import com.postech.mecanica.domain.repository.OrdemServicoStatusHistoricoRepository;
import com.postech.mecanica.exception.ResourceNotFoundException;
import com.postech.mecanica.infrastructure.persistence.entity.OrdemServicoHistoricoEntity;

@ExtendWith(MockitoExtension.class)
class BuscarTemposPorOrdemServicoUseCaseTest {

    @Mock
    private OrdemServicoRepository ordemServicoRepository;

    @Mock
    private OrdemServicoStatusHistoricoRepository ordemServicoStatusHistoricoRepository;

    @Mock
    private OrdemServicoStatusHistoricoMapper ordemServicoStatusHistoricoMapper;

    @InjectMocks
    private BuscarTemposPorOrdemServicoUseCase useCase;

    @Test
    @DisplayName("Deve buscar tempos por ordem de serviço com sucesso")
    void deveBuscarTemposComSucesso() {
        Long id = 1L;
        OrdemServico os = mock(OrdemServico.class);
        OrdemServicoHistoricoEntity historico = mock(OrdemServicoHistoricoEntity.class);
        TempoPorFaseResponseDTO dto = new TempoPorFaseResponseDTO();
        dto.setStatus(StatusOrdemServico.CRIADA);
        dto.setTempoMinutos(15L);

        when(ordemServicoRepository.consultarPorId(id)).thenReturn(Optional.of(os));
        when(ordemServicoStatusHistoricoRepository.consultarPorOrdemServicoId(id)).thenReturn(List.of(historico));
        when(ordemServicoStatusHistoricoMapper.toResponseDTO(historico)).thenReturn(dto);

        List<TempoPorFaseResponseDTO> resultado = useCase.executar(id);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(StatusOrdemServico.CRIADA, resultado.get(0).getStatus());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando ordem de serviço não for encontrada")
    void deveLancarExcecaoQuandoOsNaoEncontrada() {
        Long id = 99L;
        when(ordemServicoRepository.consultarPorId(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> useCase.executar(id));
    }
}
