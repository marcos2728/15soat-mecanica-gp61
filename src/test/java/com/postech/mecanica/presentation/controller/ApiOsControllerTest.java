package com.postech.mecanica.presentation.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

import com.postech.mecanica.application.dto.OrdemServicoResponseDTO;
import com.postech.mecanica.application.useCase.ordemservico.AprovarOrdemServicoUseCase;
import com.postech.mecanica.application.useCase.ordemservico.RecusarOrdemServicoUseCase;
import com.postech.mecanica.domain.enums.StatusOrdemServico;

@ExtendWith(MockitoExtension.class)
class ApiOsControllerTest {

    @Mock
    private AprovarOrdemServicoUseCase aprovarOrdemServicoUseCase;

    @Mock
    private RecusarOrdemServicoUseCase recusarOrdemServicoUseCase;

    @InjectMocks
    private ApiOsController apiOsController;

    private MockMvc mockMvc;
    private OrdemServicoResponseDTO aprovadaResponse;
    private OrdemServicoResponseDTO canceladaResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(apiOsController).build();
        aprovadaResponse = new OrdemServicoResponseDTO(
                1L, 10L, "ABC1D23", StatusOrdemServico.APROVADA, "Revisão", List.of(), List.of(), 100.0,
                LocalDateTime.now(), LocalDateTime.now()
        );
        canceladaResponse = new OrdemServicoResponseDTO(
                1L, 10L, "ABC1D23", StatusOrdemServico.CANCELADA, "Revisão", List.of(), List.of(), 100.0,
                LocalDateTime.now(), LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("POST /api/os/{id}/aprovar deve aprovar OS e retornar JSON")
    void deveAprovarViaPost() throws Exception {
        when(aprovarOrdemServicoUseCase.executar(1L)).thenReturn(aprovadaResponse);

        mockMvc.perform(post("/api/os/1/aprovar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APROVADA"));
    }

    @Test
    @DisplayName("GET /api/os/{id}/aprovar sem header HTML deve retornar JSON")
    void deveAprovarViaGetJson() throws Exception {
        when(aprovarOrdemServicoUseCase.executar(1L)).thenReturn(aprovadaResponse);

        mockMvc.perform(get("/api/os/1/aprovar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APROVADA"));
    }

    @Test
    @DisplayName("GET /api/os/{id}/aprovar com header HTML deve retornar página HTML")
    void deveAprovarViaGetHtml() throws Exception {
        when(aprovarOrdemServicoUseCase.executar(1L)).thenReturn(aprovadaResponse);

        mockMvc.perform(get("/api/os/1/aprovar").header("Accept", "text/html"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML));
    }

    @Test
    @DisplayName("POST /api/os/{id}/recusar deve recusar OS e retornar JSON")
    void deveRecusarViaPost() throws Exception {
        when(recusarOrdemServicoUseCase.executar(1L)).thenReturn(canceladaResponse);

        mockMvc.perform(post("/api/os/1/recusar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELADA"));
    }

    @Test
    @DisplayName("GET /api/os/{id}/recusar sem header HTML deve retornar JSON")
    void deveRecusarViaGetJson() throws Exception {
        when(recusarOrdemServicoUseCase.executar(1L)).thenReturn(canceladaResponse);

        mockMvc.perform(get("/api/os/1/recusar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELADA"));
    }

    @Test
    @DisplayName("GET /api/os/{id}/recusar com header HTML deve retornar página HTML")
    void deveRecusarViaGetHtml() throws Exception {
        when(recusarOrdemServicoUseCase.executar(1L)).thenReturn(canceladaResponse);

        mockMvc.perform(get("/api/os/1/recusar").header("Accept", "text/html"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML));
    }
}
