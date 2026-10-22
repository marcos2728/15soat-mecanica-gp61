package com.postech.mecanica.application.useCase.servico;

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

import com.postech.mecanica.application.dto.ServicoResponseDTO;
import com.postech.mecanica.application.mapper.ServicoMapper;
import com.postech.mecanica.domain.entity.Servico;
import com.postech.mecanica.domain.repository.ServicoRepository;
import com.postech.mecanica.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class ConsultarServicoUseCaseTest {

    @Mock
    private ServicoRepository servicoRepository;

    @Mock
    private ServicoMapper servicoMapper;

    @InjectMocks
    private ConsultarServicoUseCase consultarServicoUseCase;

    @Test
    @DisplayName("Deve consultar serviço por ID com sucesso")
    void deveConsultarServicoPorIdComSucesso() {
        Long id = 1L;
        Servico servico = new Servico(id, "Balanceamento", 80.0);
        ServicoResponseDTO responseDTO = new ServicoResponseDTO(id, "Balanceamento", 80.0);

        when(servicoRepository.consultarPorId(id)).thenReturn(Optional.of(servico));
        when(servicoMapper.toResponseDTO(servico)).thenReturn(responseDTO);

        ServicoResponseDTO resultado = consultarServicoUseCase.executar(id);

        assertNotNull(resultado);
        assertEquals(id, resultado.id());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando serviço não for encontrado")
    void deveLancarExcecaoQuandoServicoNaoEncontrado() {
        Long id = 99L;
        when(servicoRepository.consultarPorId(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> consultarServicoUseCase.executar(id));
    }
}
