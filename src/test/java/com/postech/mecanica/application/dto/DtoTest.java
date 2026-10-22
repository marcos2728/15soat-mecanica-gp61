package com.postech.mecanica.application.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.postech.mecanica.domain.enums.StatusOrdemServico;

class DtoTest {

    @Test
    @DisplayName("Deve testar getters e setters dos DTOs POJO")
    void deveTestarPojos() {
        LoginRequestDTO loginReq = new LoginRequestDTO();
        loginReq.setLogin("admin");
        loginReq.setSenha("123");
        assertEquals("admin", loginReq.getLogin());
        assertEquals("123", loginReq.getSenha());

        LoginResponseDTO loginRes = new LoginResponseDTO("token123");
        assertEquals("token123", loginRes.getToken());
        loginRes.setToken("token456");
        assertEquals("token456", loginRes.getToken());

        ItemEstoqueRequestDTO itemEstoqueReq = new ItemEstoqueRequestDTO();
        itemEstoqueReq.setEstoqueId(1L);
        itemEstoqueReq.setQuantidade(5);
        assertEquals(1L, itemEstoqueReq.getEstoqueId());
        assertEquals(5, itemEstoqueReq.getQuantidade());

        ItemEstoqueResponseDTO itemEstoqueRes = new ItemEstoqueResponseDTO();
        itemEstoqueRes.setEstoqueId(2L);
        itemEstoqueRes.setNomePeca("Peca2");
        itemEstoqueRes.setQuantidade(3);
        itemEstoqueRes.setValorUnitario(60.0);
        itemEstoqueRes.setDisponivel(false);
        assertEquals(2L, itemEstoqueRes.getEstoqueId());
        assertEquals("Peca2", itemEstoqueRes.getNomePeca());
        assertEquals(3, itemEstoqueRes.getQuantidade());
        assertEquals(60.0, itemEstoqueRes.getValorUnitario());
        assertEquals(false, itemEstoqueRes.getDisponivel());

        ItemServicoRequestDTO itemServicoReq = new ItemServicoRequestDTO();
        itemServicoReq.setServicoId(10L);
        assertEquals(10L, itemServicoReq.getServicoId());

        ItemServicoResponseDTO itemServicoRes = new ItemServicoResponseDTO();
        itemServicoRes.setServicoId(20L);
        itemServicoRes.setNomeServico("Serv2");
        itemServicoRes.setValorAplicado(150.0);
        assertEquals(20L, itemServicoRes.getServicoId());
        assertEquals("Serv2", itemServicoRes.getNomeServico());
        assertEquals(150.0, itemServicoRes.getValorAplicado());

        AtualizarStatusOrdemServicoRequestDTO statusReq = new AtualizarStatusOrdemServicoRequestDTO();
        statusReq.setStatus(StatusOrdemServico.APROVADA);
        assertEquals(StatusOrdemServico.APROVADA, statusReq.getStatus());

        LocalDateTime now = LocalDateTime.now();
        ApiErrorDTO apiError = new ApiErrorDTO(400, "Bad Request", "Mensagem", now);
        assertEquals(400, apiError.getStatus());
        assertEquals("Bad Request", apiError.getTitulo());
        assertEquals("Mensagem", apiError.getMensagem());
        assertEquals(now, apiError.getDataHora());

        TempoPorFaseResponseDTO tempoDTO = new TempoPorFaseResponseDTO();
        tempoDTO.setStatus(StatusOrdemServico.CRIADA);
        tempoDTO.setDataHoraInicio(now);
        tempoDTO.setDataHoraFim(now.plusMinutes(15));
        tempoDTO.setTempoMinutos(15L);
        tempoDTO.setEmAndamento(false);
        assertEquals(StatusOrdemServico.CRIADA, tempoDTO.getStatus());
        assertNotNull(tempoDTO.getDataHoraInicio());
        assertNotNull(tempoDTO.getDataHoraFim());
        assertEquals(15L, tempoDTO.getTempoMinutos());
        assertEquals(false, tempoDTO.getEmAndamento());

        TempoMedioPorFaseResponseDTO mediaDTO = new TempoMedioPorFaseResponseDTO(StatusOrdemServico.CRIADA, 30L, 5);
        assertEquals(StatusOrdemServico.CRIADA, mediaDTO.getStatus());
        assertEquals(30L, mediaDTO.getTempoMedioMinutos());
        assertEquals(5, mediaDTO.getQuantidadeAmostras());
        mediaDTO.setStatus(StatusOrdemServico.FINALIZADA);
        mediaDTO.setTempoMedioMinutos(40L);
        mediaDTO.setQuantidadeAmostras(10);
        assertEquals(StatusOrdemServico.FINALIZADA, mediaDTO.getStatus());
        assertEquals(40L, mediaDTO.getTempoMedioMinutos());
        assertEquals(10, mediaDTO.getQuantidadeAmostras());
        assertNotNull(new TempoMedioPorFaseResponseDTO());
    }

    @Test
    @DisplayName("Deve testar Records DTOs")
    void deveTestarRecords() {
        ClienteRequestDTO cliReq = new ClienteRequestDTO("Nome", "1990-01-01", "email@test.com", "11999998888");
        assertEquals("Nome", cliReq.nome());

        ClienteResponseDTO cliRes = new ClienteResponseDTO(1L, "Nome", "12345678901", "email@test.com", "11999998888", "1990-01-01");
        assertEquals(1L, cliRes.id());
        assertEquals("Nome", cliRes.nome());
        assertEquals("12345678901", cliRes.cpfCnpj());

        VeiculoRequestDTO veiReq = new VeiculoRequestDTO(1L, "ABC1D23", "Gol", "VW", 2020);
        assertEquals("ABC1D23", veiReq.placa());

        VeiculoResponseDTO veiRes = new VeiculoResponseDTO(1L, "ABC1D23", "Gol", "VW", 2020);
        assertEquals(1L, veiRes.id());

        ClienteVeiculoRequestDTO cliVeiReq = new ClienteVeiculoRequestDTO("12345678901", "Nome", "1990-01-01", "email@test.com", "11999998888", veiReq);
        assertEquals(veiReq, cliVeiReq.veiculo());

        ClienteVeiculoResponseDTO cliVeiRes = new ClienteVeiculoResponseDTO(1L, "Nome", "12345678901", "email@test.com", "11999998888", "1990-01-01", List.of(veiRes));
        assertEquals(1, cliVeiRes.veiculos().size());
        assertEquals("Nome", cliVeiRes.nome());

        EstoqueRequestDTO estReq = new EstoqueRequestDTO("Peca", 10, 50.0);
        assertEquals(10, estReq.quantidadeItem());

        EstoqueResponseDTO estRes = new EstoqueResponseDTO(1L, "Peca", 10, 50.0, List.of(100L));
        assertEquals(1, estRes.ordensLiberadas().size());
        assertNotNull(new EstoqueResponseDTO(1L, "Peca", 10, 50.0));

        ServicoDTO servDTO = new ServicoDTO(1L, "Serv", 80.0);
        assertEquals("Serv", servDTO.nomeServico());

        ServicoRequestDTO servReq = new ServicoRequestDTO("Serv", 80.0);
        assertEquals("Serv", servReq.nomeServico());

        ServicoResponseDTO servRes = new ServicoResponseDTO(1L, "Serv", 80.0);
        assertEquals(80.0, servRes.valorServico());

        OrdemServicoRequestDTO osReq = new OrdemServicoRequestDTO(1L);
        assertEquals(1L, osReq.veiculoId());

        OrdemServicoResponseDTO osRes = new OrdemServicoResponseDTO(1L, 1L, "ABC1D23", StatusOrdemServico.CRIADA, "Desc", List.of(), List.of(), 100.0, LocalDateTime.now(), null);
        assertEquals("Desc", osRes.descricaoProblema());

        DiagnosticoConcluidoRequestDTO diagReq = new DiagnosticoConcluidoRequestDTO("Prob", List.of(), List.of());
        assertEquals("Prob", diagReq.getDescricaoProblema());
        assertEquals(0, diagReq.getPecas().size());
        assertEquals(0, diagReq.getServicos().size());
    }
}
