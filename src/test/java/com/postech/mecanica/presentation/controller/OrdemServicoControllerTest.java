package com.postech.mecanica.presentation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
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

import com.postech.mecanica.application.dto.DiagnosticoConcluidoRequestDTO;
import com.postech.mecanica.application.dto.OrdemServicoRequestDTO;
import com.postech.mecanica.application.dto.OrdemServicoResponseDTO;
import com.postech.mecanica.application.dto.TempoMedioPorFaseResponseDTO;
import com.postech.mecanica.application.dto.TempoPorFaseResponseDTO;
import com.postech.mecanica.application.useCase.ordemservico.AprovarOrdemServicoUseCase;
import com.postech.mecanica.application.useCase.ordemservico.AtualizarDiagnosticoConlcuidoUseCase;
import com.postech.mecanica.application.useCase.ordemservico.AtualizarStatusOrdemUseCase;
import com.postech.mecanica.application.useCase.ordemservico.BuscarTemposPorOrdemServicoUseCase;
import com.postech.mecanica.application.useCase.ordemservico.CalcularTempoMedioPorFaseUseCase;
import com.postech.mecanica.application.useCase.ordemservico.ConsultarOrdemClienteUseCase;
import com.postech.mecanica.application.useCase.ordemservico.ConsultarOrdemServicoUseCase;
import com.postech.mecanica.application.useCase.ordemservico.CriarOrdemServicoUseCase;
import com.postech.mecanica.application.useCase.ordemservico.RecusarOrdemServicoUseCase;
import com.postech.mecanica.domain.enums.StatusOrdemServico;

@ExtendWith(MockitoExtension.class)
class OrdemServicoControllerTest {

    @Mock
    private CriarOrdemServicoUseCase criarOrdemServicoUseCase;

    @Mock
    private ConsultarOrdemServicoUseCase consultarOrdemServicoUseCase;

    @Mock
    private ConsultarOrdemClienteUseCase consultarOrdemClienteUseCase;

    @Mock
    private AtualizarStatusOrdemUseCase atualizarStatusOrdemUseCase;

    @Mock
    private AtualizarDiagnosticoConlcuidoUseCase atualizarDiagnosticoConlcuidoUseCase;

    @Mock
    private BuscarTemposPorOrdemServicoUseCase buscarTemposPorOrdemServicoUseCase;

    @Mock
    private CalcularTempoMedioPorFaseUseCase calcularTempoMedioPorFaseUseCase;

    @Mock
    private AprovarOrdemServicoUseCase aprovarOrdemServicoUseCase;

    @Mock
    private RecusarOrdemServicoUseCase recusarOrdemServicoUseCase;

    @InjectMocks
    private OrdemServicoController ordemServicoController;

    private MockMvc mockMvc;
    private OrdemServicoResponseDTO osResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(ordemServicoController).build();
        osResponse = new OrdemServicoResponseDTO(
                1L, 10L, "ABC1D23", StatusOrdemServico.CRIADA, "Revisão", List.of(), List.of(), 100.0,
                LocalDateTime.now(), null
        );
    }

    @Test
    @DisplayName("POST /ordens-servicos deve criar OS e retornar 201")
    void deveCriarOrdemServico() throws Exception {
        when(criarOrdemServicoUseCase.executar(any(OrdemServicoRequestDTO.class))).thenReturn(osResponse);

        String json = """
                {
                    "veiculoId": 10
                }
                """;

        mockMvc.perform(post("/ordens-servicos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /ordens-servicos/{id} deve consultar OS por ID")
    void deveConsultarOrdemServico() throws Exception {
        when(consultarOrdemServicoUseCase.executar(1L)).thenReturn(osResponse);

        mockMvc.perform(get("/ordens-servicos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /ordens-servicos/cliente/{documento} deve consultar ordens por cliente")
    void deveConsultarPorCliente() throws Exception {
        when(consultarOrdemClienteUseCase.executar("12345678901")).thenReturn(List.of(osResponse));

        mockMvc.perform(get("/ordens-servicos/cliente/12345678901"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @DisplayName("PATCH /ordens-servicos/{id}/status deve atualizar status")
    void deveAtualizarStatus() throws Exception {
        when(atualizarStatusOrdemUseCase.executar(eq(1L), eq(StatusOrdemServico.EM_DIAGNOSTICO))).thenReturn(osResponse);

        String json = """
                {
                    "status": "EM_DIAGNOSTICO"
                }
                """;

        mockMvc.perform(patch("/ordens-servicos/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("PATCH /ordens-servicos/{id}/diagnostico-concluido deve concluir diagnóstico")
    void deveConcluirDiagnostico() throws Exception {
        when(atualizarDiagnosticoConlcuidoUseCase.executar(eq(1L), any(DiagnosticoConcluidoRequestDTO.class))).thenReturn(osResponse);

        String json = """
                {
                    "descricaoProblema": "Freio gasto",
                    "pecas": [],
                    "servicos": []
                }
                """;

        mockMvc.perform(patch("/ordens-servicos/1/diagnostico-concluido")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /ordens-servicos/{id}/tempos deve retornar histórico de tempos")
    void deveBuscarTempos() throws Exception {
        TempoPorFaseResponseDTO tempoDTO = new TempoPorFaseResponseDTO();
        tempoDTO.setStatus(StatusOrdemServico.CRIADA);
        tempoDTO.setTempoMinutos(20L);
        when(buscarTemposPorOrdemServicoUseCase.executar(1L)).thenReturn(List.of(tempoDTO));

        mockMvc.perform(get("/ordens-servicos/1/tempos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tempoMinutos").value(20));
    }

    @Test
    @DisplayName("GET /ordens-servicos/metricas/tempo-medio-por-fase deve retornar tempos médios")
    void deveRetornarTempoMedioPorFase() throws Exception {
        TempoMedioPorFaseResponseDTO mediaDTO = new TempoMedioPorFaseResponseDTO(StatusOrdemServico.CRIADA, 30L, 5);
        when(calcularTempoMedioPorFaseUseCase.executar()).thenReturn(List.of(mediaDTO));

        mockMvc.perform(get("/ordens-servicos/metricas/tempo-medio-por-fase"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tempoMedioMinutos").value(30));
    }

    @Test
    @DisplayName("POST /ordens-servicos/{id}/aprovar deve aprovar OS")
    void deveAprovarOS() throws Exception {
        when(aprovarOrdemServicoUseCase.executar(1L)).thenReturn(osResponse);

        mockMvc.perform(post("/ordens-servicos/1/aprovar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /ordens-servicos/{id}/aprovar sem header HTML deve retornar JSON")
    void deveAprovarViaGetJson() throws Exception {
        when(aprovarOrdemServicoUseCase.executar(1L)).thenReturn(osResponse);

        mockMvc.perform(get("/ordens-servicos/1/aprovar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /ordens-servicos/{id}/aprovar com header HTML deve retornar HTML")
    void deveAprovarViaGetHtml() throws Exception {
        when(aprovarOrdemServicoUseCase.executar(1L)).thenReturn(osResponse);

        mockMvc.perform(get("/ordens-servicos/1/aprovar").header("Accept", "text/html"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML));
    }

    @Test
    @DisplayName("POST /ordens-servicos/{id}/recusar deve recusar OS")
    void deveRecusarOS() throws Exception {
        when(recusarOrdemServicoUseCase.executar(1L)).thenReturn(osResponse);

        mockMvc.perform(post("/ordens-servicos/1/recusar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /ordens-servicos/{id}/recusar sem header HTML deve retornar JSON")
    void deveRecusarViaGetJson() throws Exception {
        when(recusarOrdemServicoUseCase.executar(1L)).thenReturn(osResponse);

        mockMvc.perform(get("/ordens-servicos/1/recusar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /ordens-servicos/{id}/recusar com header HTML deve retornar HTML")
    void deveRecusarViaGetHtml() throws Exception {
        when(recusarOrdemServicoUseCase.executar(1L)).thenReturn(osResponse);

        mockMvc.perform(get("/ordens-servicos/1/recusar").header("Accept", "text/html"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML));
    }
}
