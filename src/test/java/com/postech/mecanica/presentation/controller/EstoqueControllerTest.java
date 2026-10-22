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

import com.postech.mecanica.application.dto.EstoqueRequestDTO;
import com.postech.mecanica.application.dto.EstoqueResponseDTO;
import com.postech.mecanica.application.useCase.estoque.AtualizarItemEstoqueUseCase;
import com.postech.mecanica.application.useCase.estoque.CadastrarItemEstoqueUseCase;
import com.postech.mecanica.application.useCase.estoque.ConsultarItemEstoqueUseCase;
import com.postech.mecanica.application.useCase.estoque.ConsultarTodasItensEstoqueUseCase;
import com.postech.mecanica.application.useCase.estoque.DeletarItemEstoqueUseCase;

@ExtendWith(MockitoExtension.class)
class EstoqueControllerTest {

    @Mock
    private CadastrarItemEstoqueUseCase cadastrarItemEstoqueUseCase;

    @Mock
    private ConsultarTodasItensEstoqueUseCase consultarTodasItensEstoqueUseCase;

    @Mock
    private ConsultarItemEstoqueUseCase consultarItemEstoqueUseCase;

    @Mock
    private AtualizarItemEstoqueUseCase atualizarItemEstoqueUseCase;

    @Mock
    private DeletarItemEstoqueUseCase deletarItemEstoqueUseCase;

    @InjectMocks
    private EstoqueController estoqueController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(estoqueController).build();
    }

    @Test
    @DisplayName("POST /estoque deve criar item e retornar 201")
    void deveSalvarItem() throws Exception {
        EstoqueResponseDTO response = new EstoqueResponseDTO(1L, "Óleo Motor", 10, 45.0, List.of());
        when(cadastrarItemEstoqueUseCase.executar(any(EstoqueRequestDTO.class))).thenReturn(response);

        String json = """
                {
                    "nomeItem": "Óleo Motor",
                    "quantidadeItem": 10,
                    "valorItem": 45.0
                }
                """;

        mockMvc.perform(post("/estoque")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nomeItem").value("Óleo Motor"));
    }

    @Test
    @DisplayName("GET /estoque deve retornar lista de itens")
    void deveListarTodos() throws Exception {
        EstoqueResponseDTO response = new EstoqueResponseDTO(1L, "Óleo Motor", 10, 45.0, List.of());
        when(consultarTodasItensEstoqueUseCase.executar()).thenReturn(List.of(response));

        mockMvc.perform(get("/estoque"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nomeItem").value("Óleo Motor"));
    }

    @Test
    @DisplayName("GET /estoque/{id} deve retornar item por ID")
    void deveConsultarPorId() throws Exception {
        EstoqueResponseDTO response = new EstoqueResponseDTO(1L, "Óleo Motor", 10, 45.0, List.of());
        when(consultarItemEstoqueUseCase.executar(1L)).thenReturn(response);

        mockMvc.perform(get("/estoque/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("PUT /estoque/{id} deve atualizar item e retornar 200")
    void deveAtualizarItem() throws Exception {
        EstoqueResponseDTO response = new EstoqueResponseDTO(1L, "Óleo Motor Sintético", 20, 55.0, List.of());
        when(atualizarItemEstoqueUseCase.executar(eq(1L), any(EstoqueRequestDTO.class))).thenReturn(response);

        String json = """
                {
                    "nomeItem": "Óleo Motor Sintético",
                    "quantidadeItem": 20,
                    "valorItem": 55.0
                }
                """;

        mockMvc.perform(put("/estoque/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeItem").value("Óleo Motor Sintético"));
    }

    @Test
    @DisplayName("DELETE /estoque/{id} deve deletar item e retornar 204")
    void deveDeletarItem() throws Exception {
        mockMvc.perform(delete("/estoque/1"))
                .andExpect(status().isNoContent());

        verify(deletarItemEstoqueUseCase).executar(1L);
    }
}
