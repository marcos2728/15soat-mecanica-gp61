package com.postech.mecanica.presentation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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

import com.postech.mecanica.application.dto.VeiculoRequestDTO;
import com.postech.mecanica.application.dto.VeiculoResponseDTO;
import com.postech.mecanica.application.useCase.veiculo.AtualizarVeiculoUseCase;
import com.postech.mecanica.application.useCase.veiculo.CadastrarVeiculoUseCase;
import com.postech.mecanica.application.useCase.veiculo.ConsultarVeiculoUseCase;
import com.postech.mecanica.application.useCase.veiculo.ConsultarVeiculosClienteUseCase;
import com.postech.mecanica.application.useCase.veiculo.DeletarVeiculoUseCase;

@ExtendWith(MockitoExtension.class)
class VeiculoControllerTest {

    @Mock
    private CadastrarVeiculoUseCase cadastrarVeiculoUseCase;

    @Mock
    private AtualizarVeiculoUseCase atualizarVeiculoUsecase;

    @Mock
    private ConsultarVeiculoUseCase consultarVeiculoUsecase;

    @Mock
    private ConsultarVeiculosClienteUseCase consultarVeiculosClienteUseCase;

    @Mock
    private DeletarVeiculoUseCase deletarVeiculoUseCase;

    @InjectMocks
    private VeiculoController veiculoController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(veiculoController).build();
    }

    @Test
    @DisplayName("POST /veiculos/{idCliente} deve cadastrar veículo e retornar dados")
    void deveCadastrarVeiculo() throws Exception {
        VeiculoResponseDTO response = new VeiculoResponseDTO(1L, "ABC1D23", "Civic", "Honda", 2022);
        when(cadastrarVeiculoUseCase.executar(eq(1L), any(VeiculoRequestDTO.class))).thenReturn(response);

        String json = """
                {
                    "placa": "ABC1D23",
                    "modelo": "Civic",
                    "marca": "Honda",
                    "anoFabricacao": 2022
                }
                """;

        mockMvc.perform(post("/veiculos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.placa").value("ABC1D23"));
    }

    @Test
    @DisplayName("GET /veiculos/cliente/{documento} deve consultar veículos do cliente")
    void deveConsultarVeiculosDoCliente() throws Exception {
        VeiculoResponseDTO response = new VeiculoResponseDTO(1L, "ABC1D23", "Civic", "Honda", 2022);
        when(consultarVeiculosClienteUseCase.executar("12345678901")).thenReturn(List.of(response));

        mockMvc.perform(get("/veiculos/cliente/12345678901"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].placa").value("ABC1D23"));
    }

    @Test
    @DisplayName("GET /veiculos/{id} deve consultar veículo por ID")
    void deveConsultarPorId() throws Exception {
        VeiculoResponseDTO response = new VeiculoResponseDTO(1L, "ABC1D23", "Civic", "Honda", 2022);
        when(consultarVeiculoUsecase.executar(1L)).thenReturn(response);

        mockMvc.perform(get("/veiculos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("PUT /veiculos/{id} deve atualizar veículo e retornar mensagem de sucesso")
    void deveAtualizarVeiculo() throws Exception {
        String json = """
                {
                    "placa": "ABC1D23",
                    "modelo": "Civic Touring",
                    "marca": "Honda",
                    "anoFabricacao": 2023
                }
                """;

        mockMvc.perform(put("/veiculos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("Veículo atualizado com sucesso"));

        verify(atualizarVeiculoUsecase).executar(eq(1L), any(VeiculoRequestDTO.class));
    }

    @Test
    @DisplayName("DELETE /veiculos/{id} deve deletar veículo e retornar 204")
    void deveDeletarVeiculo() throws Exception {
        mockMvc.perform(delete("/veiculos/1"))
                .andExpect(status().isNoContent());

        verify(deletarVeiculoUseCase).executar(1L);
    }
}
