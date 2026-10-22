package com.postech.mecanica.presentation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.postech.mecanica.application.dto.ServicoRequestDTO;
import com.postech.mecanica.application.dto.ServicoResponseDTO;
import com.postech.mecanica.application.useCase.servico.AtualizarServicoUseCase;
import com.postech.mecanica.application.useCase.servico.CadastrarServicoUseCase;
import com.postech.mecanica.application.useCase.servico.ConsultarServicoUseCase;
import com.postech.mecanica.application.useCase.servico.ConsultarTodosServicosUseCase;
import com.postech.mecanica.application.useCase.servico.DeletarServicoUseCase;

@ExtendWith(MockitoExtension.class)
class ServicoControllerTest {

    @Mock
    private CadastrarServicoUseCase cadastrarServicoUseCase;

    @Mock
    private ConsultarTodosServicosUseCase consultarTodosServicosUseCase;

    @Mock
    private ConsultarServicoUseCase consultarServicoUseCase;

    @Mock
    private AtualizarServicoUseCase atualizarServicoUseCase;

    @Mock
    private DeletarServicoUseCase deletarServicoUseCase;

    @InjectMocks
    private ServicoController servicoController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(servicoController).build();
    }

    @Test
    @DisplayName("POST /servicos deve criar serviço e retornar 201")
    void deveCriarServico() throws Exception {
        ServicoResponseDTO response = new ServicoResponseDTO(1L, "Alinhamento", 120.0);
        when(cadastrarServicoUseCase.executar(any(ServicoRequestDTO.class))).thenReturn(response);

        String json = """
                {
                    "nomeServico": "Alinhamento",
                    "valorServico": 120.0
                }
                """;

        mockMvc.perform(post("/servicos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nomeServico").value("Alinhamento"));
    }

    @Test
    @DisplayName("GET /servicos deve listar serviços")
    void deveListarServicos() throws Exception {
        ServicoResponseDTO response = new ServicoResponseDTO(1L, "Alinhamento", 120.0);
        when(consultarTodosServicosUseCase.executar()).thenReturn(List.of(response));

        mockMvc.perform(get("/servicos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nomeServico").value("Alinhamento"));
    }

    @Test
    @DisplayName("GET /servicos/{id} deve obter serviço por ID")
    void deveObterPorId() throws Exception {
        ServicoResponseDTO response = new ServicoResponseDTO(1L, "Alinhamento", 120.0);
        when(consultarServicoUseCase.executar(1L)).thenReturn(response);

        mockMvc.perform(get("/servicos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("PUT /servicos/{id} deve atualizar serviço")
    void deveAtualizarServico() throws Exception {
        ServicoResponseDTO response = new ServicoResponseDTO(1L, "Alinhamento 3D", 150.0);
        when(atualizarServicoUseCase.executar(eq(1L), any(ServicoRequestDTO.class))).thenReturn(response);

        String json = """
                {
                    "nomeServico": "Alinhamento 3D",
                    "valorServico": 150.0
                }
                """;

        mockMvc.perform(put("/servicos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeServico").value("Alinhamento 3D"));
    }

    @Test
    @DisplayName("DELETE /servicos/{id} deve deletar serviço e retornar 204")
    void deveDeletarServico() throws Exception {
        mockMvc.perform(delete("/servicos/1"))
                .andExpect(status().isNoContent());

        verify(deletarServicoUseCase).executar(1L);
    }
}
