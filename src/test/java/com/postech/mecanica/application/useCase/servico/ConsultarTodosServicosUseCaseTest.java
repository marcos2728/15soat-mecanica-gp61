package com.postech.mecanica.application.useCase.servico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.postech.mecanica.application.dto.ServicoResponseDTO;
import com.postech.mecanica.application.mapper.ServicoMapper;
import com.postech.mecanica.domain.entity.Servico;
import com.postech.mecanica.domain.repository.ServicoRepository;

@ExtendWith(MockitoExtension.class)
class ConsultarTodosServicosUseCaseTest {

    @Mock
    private ServicoRepository servicoRepository;

    @Mock
    private ServicoMapper servicoMapper;

    @InjectMocks
    private ConsultarTodosServicosUseCase consultarTodosServicosUseCase;

    @Test
    @DisplayName("Deve consultar todos os serviços com sucesso")
    void deveConsultarTodosServicosComSucesso() {
        Servico s1 = new Servico(1L, "Alinhamento", 100.0);
        Servico s2 = new Servico(2L, "Balanceamento", 80.0);
        ServicoResponseDTO dto1 = new ServicoResponseDTO(1L, "Alinhamento", 100.0);
        ServicoResponseDTO dto2 = new ServicoResponseDTO(2L, "Balanceamento", 80.0);

        when(servicoRepository.consultarTodos()).thenReturn(List.of(s1, s2));
        when(servicoMapper.toResponseDTO(s1)).thenReturn(dto1);
        when(servicoMapper.toResponseDTO(s2)).thenReturn(dto2);

        List<ServicoResponseDTO> resultado = consultarTodosServicosUseCase.executar();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
    }
}
