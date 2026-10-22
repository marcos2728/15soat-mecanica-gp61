package com.postech.mecanica.application.useCase.ordemservico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.postech.mecanica.application.dto.OrdemServicoResponseDTO;
import com.postech.mecanica.application.mapper.OrdemServicoMapper;
import com.postech.mecanica.domain.entity.OrdemServico;
import com.postech.mecanica.domain.repository.OrdemServicoRepository;
import com.postech.mecanica.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class ConsultarOrdemServicoUseCaseTest {

    @Mock
    private OrdemServicoRepository ordemServicoRepository;

    @Mock
    private OrdemServicoMapper ordemServicoMapper;

    @InjectMocks
    private ConsultarOrdemServicoUseCase useCase;

    @Test
    @DisplayName("Deve consultar ordem de serviço por ID com sucesso")
    void deveConsultarOrdemServicoPorId() {
        Long id = 1L;
        OrdemServico os = mock(OrdemServico.class);
        OrdemServicoResponseDTO dto = mock(OrdemServicoResponseDTO.class);

        when(ordemServicoRepository.consultarPorId(id)).thenReturn(Optional.of(os));
        when(ordemServicoMapper.toResponseDTO(os)).thenReturn(dto);

        OrdemServicoResponseDTO resultado = useCase.executar(id);

        assertNotNull(resultado);
        assertEquals(dto, resultado);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando ordem de serviço não for encontrada")
    void deveLancarExcecaoQuandoNaoEncontrada() {
        Long id = 99L;
        when(ordemServicoRepository.consultarPorId(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> useCase.executar(id));
    }
}
