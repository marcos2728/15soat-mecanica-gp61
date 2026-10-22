package com.postech.mecanica.application.useCase.ordemservico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.postech.mecanica.application.dto.DiagnosticoConcluidoRequestDTO;
import com.postech.mecanica.application.dto.ItemEstoqueRequestDTO;
import com.postech.mecanica.application.dto.ItemServicoRequestDTO;
import com.postech.mecanica.application.dto.OrdemServicoResponseDTO;
import com.postech.mecanica.application.mapper.OrdemServicoMapper;
import com.postech.mecanica.domain.entity.Estoque;
import com.postech.mecanica.domain.entity.OrdemServico;
import com.postech.mecanica.domain.entity.Servico;
import com.postech.mecanica.domain.entity.Veiculo;
import com.postech.mecanica.domain.enums.StatusOrdemServico;
import com.postech.mecanica.domain.repository.EstoqueRepository;
import com.postech.mecanica.domain.repository.OrdemServicoRepository;
import com.postech.mecanica.domain.repository.ServicoRepository;
import com.postech.mecanica.domain.vo.Placa;
import com.postech.mecanica.exception.RegraNegocioException;
import com.postech.mecanica.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class AtualizarDiagnosticoConlcuidoUseCaseTest {

    @Mock
    private OrdemServicoRepository ordemServicoRepository;

    @Mock
    private OrdemServicoMapper ordemServicoMapper;

    @Mock
    private EstoqueRepository estoqueRepository;

    @Mock
    private ServicoRepository servicoRepository;

    @InjectMocks
    private AtualizarDiagnosticoConlcuidoUseCase useCase;

    private OrdemServico ordemServico;

    @BeforeEach
    void setUp() {
        Veiculo veiculo = new Veiculo(1L, new Placa("ABC1D23"), "Corsa", "Chevrolet", 2015);
        ordemServico = new OrdemServico(1L, veiculo, StatusOrdemServico.EM_DIAGNOSTICO, "Barulho na suspensão", new ArrayList<>(), new ArrayList<>(), 0.0, LocalDateTime.now(), null);
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException quando requestDTO for nulo")
    void deveLancarExcecaoQuandoRequestNulo() {
        assertThrows(RegraNegocioException.class, () -> useCase.executar(1L, null));
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException quando ordem de serviço não for encontrada")
    void deveLancarExcecaoQuandoOsNaoEncontrada() {
        when(ordemServicoRepository.consultarPorId(1L)).thenReturn(Optional.empty());
        DiagnosticoConcluidoRequestDTO request = new DiagnosticoConcluidoRequestDTO("Problema", List.of(), List.of());

        assertThrows(RegraNegocioException.class, () -> useCase.executar(1L, request));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando peça não for encontrada no estoque")
    void deveLancarExcecaoQuandoPecaNaoEncontrada() {
        when(ordemServicoRepository.consultarPorId(1L)).thenReturn(Optional.of(ordemServico));
        when(estoqueRepository.consultarPorId(10L)).thenReturn(Optional.empty());

        ItemEstoqueRequestDTO pecaItem = new ItemEstoqueRequestDTO();
        pecaItem.setEstoqueId(10L);
        pecaItem.setQuantidade(2);

        DiagnosticoConcluidoRequestDTO request = new DiagnosticoConcluidoRequestDTO("Problema", List.of(pecaItem), List.of());

        assertThrows(ResourceNotFoundException.class, () -> useCase.executar(1L, request));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando serviço mecânico não for encontrado")
    void deveLancarExcecaoQuandoServicoNaoEncontrado() {
        when(ordemServicoRepository.consultarPorId(1L)).thenReturn(Optional.of(ordemServico));
        when(servicoRepository.consultarPorId(20L)).thenReturn(Optional.empty());

        ItemServicoRequestDTO servicoItem = new ItemServicoRequestDTO();
        servicoItem.setServicoId(20L);

        DiagnosticoConcluidoRequestDTO request = new DiagnosticoConcluidoRequestDTO("Problema", null, List.of(servicoItem));

        assertThrows(ResourceNotFoundException.class, () -> useCase.executar(1L, request));
    }

    @Test
    @DisplayName("Deve concluir diagnóstico com peças disponíveis, peças indisponíveis e serviços")
    void deveConcluirDiagnosticoComSucesso() {
        when(ordemServicoRepository.consultarPorId(1L)).thenReturn(Optional.of(ordemServico));

        Estoque estoqueDisponivel = new Estoque(10L, "Amortecedor", 5, 200.0);
        Estoque estoqueIndisponivel = new Estoque(11L, "Mola", 0, 100.0);

        when(estoqueRepository.consultarPorId(10L)).thenReturn(Optional.of(estoqueDisponivel));
        when(estoqueRepository.consultarPorId(11L)).thenReturn(Optional.of(estoqueIndisponivel));

        ItemEstoqueRequestDTO itemDisp = new ItemEstoqueRequestDTO();
        itemDisp.setEstoqueId(10L);
        itemDisp.setQuantidade(2);

        ItemEstoqueRequestDTO itemIndisp = new ItemEstoqueRequestDTO();
        itemIndisp.setEstoqueId(11L);
        itemIndisp.setQuantidade(1);

        Servico servico = new Servico(20L, "Troca de amortecedor", 150.0);
        when(servicoRepository.consultarPorId(20L)).thenReturn(Optional.of(servico));

        ItemServicoRequestDTO servicoItem = new ItemServicoRequestDTO();
        servicoItem.setServicoId(20L);

        DiagnosticoConcluidoRequestDTO request = new DiagnosticoConcluidoRequestDTO(
                "Amortecedores estourados",
                List.of(itemDisp, itemIndisp),
                List.of(servicoItem)
        );

        when(ordemServicoRepository.salvar(any(OrdemServico.class))).thenAnswer(inv -> inv.getArgument(0));

        OrdemServicoResponseDTO responseDTO = new OrdemServicoResponseDTO(
                1L, 1L, "ABC1D23", StatusOrdemServico.PENDENTE_ESTOQUE, "Amortecedores estourados", List.of(), List.of(), 550.0, LocalDateTime.now(), null
        );
        when(ordemServicoMapper.toResponseDTO(any(OrdemServico.class))).thenReturn(responseDTO);

        OrdemServicoResponseDTO resultado = useCase.executar(1L, request);

        assertNotNull(resultado);
        verify(estoqueRepository).salvar(estoqueDisponivel);
        verify(ordemServicoRepository).salvar(ordemServico);
        assertEquals(3, estoqueDisponivel.getQuantidadeItem()); // baixou 2 de 5
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException ao tentar concluir diagnóstico sem peças e sem serviços")
    void deveLancarExcecaoSemPecasESemServicos() {
        when(ordemServicoRepository.consultarPorId(1L)).thenReturn(Optional.of(ordemServico));
        DiagnosticoConcluidoRequestDTO request = new DiagnosticoConcluidoRequestDTO("Revisão OK", null, null);

        assertThrows(RegraNegocioException.class, () -> useCase.executar(1L, request));
    }

    @Test
    @DisplayName("Deve concluir diagnóstico com apenas serviços mecânicos")
    void deveConcluirDiagnosticoComApenasServicos() {
        when(ordemServicoRepository.consultarPorId(1L)).thenReturn(Optional.of(ordemServico));
        Servico servico = new Servico(30L, "Mão de obra", 200.0);
        when(servicoRepository.consultarPorId(30L)).thenReturn(Optional.of(servico));

        ItemServicoRequestDTO itemServ = new ItemServicoRequestDTO();
        itemServ.setServicoId(30L);

        DiagnosticoConcluidoRequestDTO request = new DiagnosticoConcluidoRequestDTO("Revisão OK", null, List.of(itemServ));

        when(ordemServicoRepository.salvar(any(OrdemServico.class))).thenReturn(ordemServico);
        OrdemServicoResponseDTO responseDTO = new OrdemServicoResponseDTO(
                1L, 1L, "ABC1D23", StatusOrdemServico.DIAGNOSTICO_CONCLUIDO, "Revisão OK", List.of(), List.of(), 200.0, LocalDateTime.now(), null
        );
        when(ordemServicoMapper.toResponseDTO(any(OrdemServico.class))).thenReturn(responseDTO);

        OrdemServicoResponseDTO resultado = useCase.executar(1L, request);

        assertNotNull(resultado);
        verify(ordemServicoRepository).salvar(ordemServico);
    }
}
