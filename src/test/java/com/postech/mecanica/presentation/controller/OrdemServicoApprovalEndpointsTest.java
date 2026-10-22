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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.postech.mecanica.application.dto.OrdemServicoResponseDTO;
import com.postech.mecanica.application.useCase.ordemservico.AprovarOrdemServicoUseCase;
import com.postech.mecanica.application.useCase.ordemservico.RecusarOrdemServicoUseCase;
import com.postech.mecanica.domain.enums.StatusOrdemServico;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class OrdemServicoApprovalEndpointsTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AprovarOrdemServicoUseCase aprovarOrdemServicoUseCase;

    @MockitoBean
    private RecusarOrdemServicoUseCase recusarOrdemServicoUseCase;

    private OrdemServicoResponseDTO aprovadaResponse;
    private OrdemServicoResponseDTO canceladaResponse;

    @BeforeEach
    void setUp() {
        aprovadaResponse = new OrdemServicoResponseDTO(
                1L, 1L, "ABC1D23", StatusOrdemServico.APROVADA, "Problema no motor", List.of(), List.of(), 500.0,
                LocalDateTime.now(), LocalDateTime.now()
        );
        canceladaResponse = new OrdemServicoResponseDTO(
                1L, 1L, "ABC1D23", StatusOrdemServico.CANCELADA, "Problema no motor", List.of(), List.of(), 500.0,
                LocalDateTime.now(), LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("POST /ordens-servicos/{id}/aprovar deve retornar 200 OK e JSON da OS aprovada")
    void deveAprovarViaPost() throws Exception {
        when(aprovarOrdemServicoUseCase.executar(1L)).thenReturn(aprovadaResponse);

        mockMvc.perform(post("/ordens-servicos/1/aprovar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("APROVADA"));
    }

    @Test
    @DisplayName("GET /ordens-servicos/{id}/aprovar com Accept: text/html deve retornar página HTML de confirmação")
    void deveAprovarViaGetHtml() throws Exception {
        when(aprovarOrdemServicoUseCase.executar(1L)).thenReturn(aprovadaResponse);

        mockMvc.perform(get("/ordens-servicos/1/aprovar").header("Accept", "text/html"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Orçamento Aprovado com Sucesso!")));
    }

    @Test
    @DisplayName("POST /ordens-servicos/{id}/recusar deve retornar 200 OK e JSON da OS cancelada")
    void deveRecusarViaPost() throws Exception {
        when(recusarOrdemServicoUseCase.executar(1L)).thenReturn(canceladaResponse);

        mockMvc.perform(post("/ordens-servicos/1/recusar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("CANCELADA"));
    }

    @Test
    @DisplayName("GET /ordens-servicos/{id}/recusar com Accept: text/html deve retornar página HTML de recusa")
    void deveRecusarViaGetHtml() throws Exception {
        when(recusarOrdemServicoUseCase.executar(1L)).thenReturn(canceladaResponse);

        mockMvc.perform(get("/ordens-servicos/1/recusar").header("Accept", "text/html"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Orçamento Recusado")));
    }

    @Test
    @DisplayName("POST /api/os/{id}/aprovar deve funcionar de forma idêntica ao endpoint principal")
    void deveAprovarViaApiOs() throws Exception {
        when(aprovarOrdemServicoUseCase.executar(1L)).thenReturn(aprovadaResponse);

        mockMvc.perform(post("/api/os/1/aprovar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APROVADA"));
    }

    @Test
    @DisplayName("POST /api/os/{id}/recusar deve funcionar de forma idêntica ao endpoint principal")
    void deveRecusarViaApiOs() throws Exception {
        when(recusarOrdemServicoUseCase.executar(1L)).thenReturn(canceladaResponse);

        mockMvc.perform(post("/api/os/1/recusar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELADA"));
    }
}
