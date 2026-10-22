package com.postech.mecanica.application.useCase.servico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.postech.mecanica.application.dto.ServicoRequestDTO;
import com.postech.mecanica.application.dto.ServicoResponseDTO;
import com.postech.mecanica.application.mapper.ServicoMapper;
import com.postech.mecanica.domain.entity.Servico;
import com.postech.mecanica.domain.repository.ServicoRepository;

@ExtendWith(MockitoExtension.class)
class CadastrarServicoUseCaseTest {

    @Mock
    private ServicoRepository servicoRepository;

    @Mock
    private ServicoMapper servicoMapper;

    @InjectMocks
    private CadastrarServicoUseCase cadastrarServicoUseCase;

    @Test
    @DisplayName("Deve cadastrar serviço com sucesso")
    void deveCadastrarServicoComSucesso() {
        ServicoRequestDTO request = new ServicoRequestDTO("Alinhamento", 120.0);
        Servico servico = new Servico(1L, "Alinhamento", 120.0);
        ServicoResponseDTO responseDTO = new ServicoResponseDTO(1L, "Alinhamento", 120.0);

        when(servicoMapper.toDomain(request)).thenReturn(servico);
        when(servicoRepository.salvar(servico)).thenReturn(servico);
        when(servicoMapper.toResponseDTO(servico)).thenReturn(responseDTO);

        ServicoResponseDTO resultado = cadastrarServicoUseCase.executar(request);

        assertNotNull(resultado);
        assertEquals("Alinhamento", resultado.nomeServico());
        verify(servicoRepository).salvar(servico);
    }
}
