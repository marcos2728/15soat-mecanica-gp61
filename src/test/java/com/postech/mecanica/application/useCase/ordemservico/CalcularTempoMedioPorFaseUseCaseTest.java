package com.postech.mecanica.application.useCase.ordemservico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.postech.mecanica.application.dto.TempoMedioPorFaseResponseDTO;
import com.postech.mecanica.application.mapper.OrdemServicoStatusHistoricoMapper;
import com.postech.mecanica.domain.enums.StatusOrdemServico;
import com.postech.mecanica.domain.repository.OrdemServicoStatusHistoricoRepository;
import com.postech.mecanica.infrastructure.persistence.entity.OrdemServicoHistoricoEntity;

@ExtendWith(MockitoExtension.class)
class CalcularTempoMedioPorFaseUseCaseTest {

    @Mock
    private OrdemServicoStatusHistoricoRepository ordemServicoStatusHistoricoRepository;

    @Mock
    private OrdemServicoStatusHistoricoMapper ordemServicoStatusHistoricoMapper;

    @InjectMocks
    private CalcularTempoMedioPorFaseUseCase useCase;

    @Test
    @DisplayName("Deve calcular tempo médio por fase com sucesso")
    void deveCalcularTempoMedioPorFase() {
        LocalDateTime inicio1 = LocalDateTime.of(2026, 1, 1, 10, 0);
        LocalDateTime fim1 = LocalDateTime.of(2026, 1, 1, 10, 30); // 30 min
        OrdemServicoHistoricoEntity h1 = new OrdemServicoHistoricoEntity();
        h1.setStatus(StatusOrdemServico.CRIADA);
        h1.setDataHoraInicio(inicio1);
        h1.setDataHoraFim(fim1);

        LocalDateTime inicio2 = LocalDateTime.of(2026, 1, 1, 11, 0);
        LocalDateTime fim2 = LocalDateTime.of(2026, 1, 1, 12, 0); // 60 min
        OrdemServicoHistoricoEntity h2 = new OrdemServicoHistoricoEntity();
        h2.setStatus(StatusOrdemServico.CRIADA);
        h2.setDataHoraInicio(inicio2);
        h2.setDataHoraFim(fim2);

        when(ordemServicoStatusHistoricoRepository.findByDataHoraFimIsNotNull()).thenReturn(List.of(h1, h2));

        TempoMedioPorFaseResponseDTO dto = new TempoMedioPorFaseResponseDTO(StatusOrdemServico.CRIADA, 45L, 2);
        when(ordemServicoStatusHistoricoMapper.toMediaResponseDTO(StatusOrdemServico.CRIADA, 45L, 2)).thenReturn(dto);

        List<TempoMedioPorFaseResponseDTO> resultado = useCase.executar();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(StatusOrdemServico.CRIADA, resultado.get(0).getStatus());
        assertEquals(45L, resultado.get(0).getTempoMedioMinutos());
        assertEquals(2, resultado.get(0).getQuantidadeAmostras());
    }
}
