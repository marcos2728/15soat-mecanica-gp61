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

import com.postech.mecanica.application.dto.ClienteRequestDTO;
import com.postech.mecanica.application.dto.ClienteVeiculoRequestDTO;
import com.postech.mecanica.application.dto.ClienteVeiculoResponseDTO;
import com.postech.mecanica.application.useCase.cliente.AtualizarClienteUseCase;
import com.postech.mecanica.application.useCase.cliente.CadastrarClienteUseCase;
import com.postech.mecanica.application.useCase.cliente.ConsultarClienteUseCase;
import com.postech.mecanica.application.useCase.cliente.DeletarClienteUseCase;

@ExtendWith(MockitoExtension.class)
class ClienteControllerTest {

    @Mock
    private CadastrarClienteUseCase cadastrarClienteUseCase;

    @Mock
    private ConsultarClienteUseCase consultarClienteUseCase;

    @Mock
    private AtualizarClienteUseCase atualizarClienteUseCase;

    @Mock
    private DeletarClienteUseCase deletarClienteUseCase;

    @InjectMocks
    private ClienteController clienteController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(clienteController).build();
    }

    @Test
    @DisplayName("POST /clientes deve criar cliente e retornar 201")
    void deveCriarCliente() throws Exception {
        ClienteVeiculoResponseDTO response = new ClienteVeiculoResponseDTO(
                1L, "João", "12345678901", "joao@email.com", "11988887777", "1990-01-01", List.of()
        );
        when(cadastrarClienteUseCase.executar(any(ClienteVeiculoRequestDTO.class))).thenReturn(response);

        String json = """
                {
                    "cpfCnpj": "12345678901",
                    "nome": "João",
                    "dataDeNascimento": "1990-01-01",
                    "email": "joao@email.com",
                    "telefone": "11988887777",
                    "veiculo": {
                        "placa": "ABC1D23",
                        "marca": "Chevrolet",
                        "modelo": "Onix",
                        "anoFabricacao": 2021
                    }
                }
                """;

        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João"));
    }

    @Test
    @DisplayName("GET /clientes/{cpfCnpj} deve retornar dados do cliente")
    void deveBuscarPorCpfCnpj() throws Exception {
        ClienteVeiculoResponseDTO response = new ClienteVeiculoResponseDTO(
                1L, "João", "12345678901", "joao@email.com", "11988887777", "1990-01-01", List.of()
        );
        when(consultarClienteUseCase.executar("12345678901")).thenReturn(response);

        mockMvc.perform(get("/clientes/12345678901"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João"));
    }

    @Test
    @DisplayName("PUT /clientes/{id} deve atualizar cliente e retornar mensagem de sucesso")
    void deveAtualizarCliente() throws Exception {
        String json = """
                {
                    "nome": "João Novo",
                    "dataDeNascimento": "1990-01-01",
                    "email": "joao.novo@email.com",
                    "telefone": "11988887777"
                }
                """;

        mockMvc.perform(put("/clientes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("Cliente atualizado com sucesso"));

        verify(atualizarClienteUseCase).executar(eq(1L), any(ClienteRequestDTO.class));
    }

    @Test
    @DisplayName("DELETE /clientes/{id} deve deletar cliente e retornar 204")
    void deveDeletarCliente() throws Exception {
        mockMvc.perform(delete("/clientes/1"))
                .andExpect(status().isNoContent());

        verify(deletarClienteUseCase).executar(1L);
    }
}
