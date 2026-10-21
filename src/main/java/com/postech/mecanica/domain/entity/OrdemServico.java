package com.postech.mecanica.domain.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.postech.mecanica.domain.enums.StatusOrdemServico;
import com.postech.mecanica.exception.RegraNegocioException;

public class OrdemServico {

    private Long id;
    private Veiculo veiculo;
    private StatusOrdemServico status;
    private String descricaoProblema;
    private final List<OrdemServicoItemEstoque> pecas = new ArrayList<>();
    private final List<OrdemServicoItemServico> servicos = new ArrayList<>();
    private Double valorTotal;
    private LocalDateTime dataHoraCriacao;
    private LocalDateTime dataHoraAtualizacao;

    public OrdemServico(Long id, Veiculo veiculo, StatusOrdemServico status, String descricaoProblema,
            List<OrdemServicoItemEstoque> pecas, List<OrdemServicoItemServico> servicos,
            Double valorTotal, LocalDateTime dataHoraCriacao, LocalDateTime dataHoraAtualizacao) {
        validarDadosObrigatorios(veiculo);
        this.id = id;
        this.veiculo = veiculo;
        this.status = status != null ? status : StatusOrdemServico.CRIADA;
        this.descricaoProblema = descricaoProblema;
        if (pecas != null) {
            this.pecas.addAll(pecas);
        }
        if (servicos != null) {
            this.servicos.addAll(servicos);
        }
        this.valorTotal = valorTotal != null ? valorTotal : calcularValorTotal();
        this.dataHoraCriacao = dataHoraCriacao != null ? dataHoraCriacao : LocalDateTime.now();
        this.dataHoraAtualizacao = dataHoraAtualizacao;
    }

    public OrdemServico(Long id, Veiculo veiculo, StatusOrdemServico status,
            List<OrdemServicoItemEstoque> pecas, List<OrdemServicoItemServico> servicos,
            Double valorTotal, LocalDateTime dataHoraCriacao, LocalDateTime dataHoraAtualizacao) {
        this(id, veiculo, status, null, pecas, servicos, valorTotal, dataHoraCriacao, dataHoraAtualizacao);
    }

    public OrdemServico(Long id, Veiculo veiculo) {
        this(id, veiculo, StatusOrdemServico.CRIADA, List.of(), List.of(), 0.0, LocalDateTime.now(), null);
    }

    public OrdemServico(Veiculo veiculo, StatusOrdemServico status, LocalDateTime dataHoraCriacao) {
        this(null, veiculo, status, List.of(), List.of(), 0.0, dataHoraCriacao, null);
    }

    public OrdemServico(Veiculo veiculo, StatusOrdemServico status) {
        this(null, veiculo, status, List.of(), List.of(), 0.0, LocalDateTime.now(), null);
    }

    public OrdemServico(Veiculo veiculo) {
        this(null, veiculo);
    }

    private void validarDadosObrigatorios(Veiculo veiculo) {
        if (veiculo == null) {
            throw new RegraNegocioException("O veículo da ordem de serviço é obrigatório.");
        }
    }

    public void transitarPara(StatusOrdemServico novoStatus) {
        if (novoStatus == null) {
            throw new RegraNegocioException("O novo status não pode ser nulo.");
        }
        if (this.status == null || !this.status.podeTransitarPara(novoStatus)) {
            throw new RegraNegocioException(
                    "Não é possível mudar o status de " + this.status + " para " + novoStatus + ".");
        }
        this.status = novoStatus;
        this.dataHoraAtualizacao = LocalDateTime.now();
    }

    public void mudarStatus(StatusOrdemServico novoStatus) {
        transitarPara(novoStatus);
    }

    public void iniciarDiagnostico() {
        transitarPara(StatusOrdemServico.EM_DIAGNOSTICO);
    }

    public void cancelar() {
        transitarPara(StatusOrdemServico.CANCELADA);
    }

    public void adicionarPeca(OrdemServicoItemEstoque peca) {
        if (peca == null) {
            throw new RegraNegocioException("A peça não pode ser nula.");
        }
        this.pecas.add(peca);
        calcularValorTotal();
        this.dataHoraAtualizacao = LocalDateTime.now();
    }

    public void removerPeca(OrdemServicoItemEstoque peca) {
        this.pecas.remove(peca);
        calcularValorTotal();
        this.dataHoraAtualizacao = LocalDateTime.now();
    }

    public void adicionarServico(OrdemServicoItemServico servico) {
        if (servico == null) {
            throw new RegraNegocioException("O serviço não pode ser nulo.");
        }
        this.servicos.add(servico);
        calcularValorTotal();
        this.dataHoraAtualizacao = LocalDateTime.now();
    }

    public void removerServico(OrdemServicoItemServico servico) {
        this.servicos.remove(servico);
        calcularValorTotal();
        this.dataHoraAtualizacao = LocalDateTime.now();
    }

    public void definirItens(List<OrdemServicoItemEstoque> novasPecas, List<OrdemServicoItemServico> novosServicos) {
        boolean semPecas = novasPecas == null || novasPecas.isEmpty();
        boolean semServicos = novosServicos == null || novosServicos.isEmpty();
        if (semPecas && semServicos) {
            throw new RegraNegocioException(
                    "É necessário informar ao menos uma peça ou serviço para a ordem de serviço.");
        }

        this.pecas.clear();
        if (novasPecas != null) {
            this.pecas.addAll(novasPecas);
        }

        this.servicos.clear();
        if (novosServicos != null) {
            this.servicos.addAll(novosServicos);
        }

        calcularValorTotal();
        this.dataHoraAtualizacao = LocalDateTime.now();
    }

    public void diagnosticoConcluido(List<OrdemServicoItemEstoque> pecas, List<OrdemServicoItemServico> servicos) {
        definirItens(pecas, servicos);
        StatusOrdemServico proximoStatus = todasPecasDisponiveis()
                ? StatusOrdemServico.DIAGNOSTICO_CONCLUIDO
                : StatusOrdemServico.PENDENTE_ESTOQUE;
        transitarPara(proximoStatus);
    }

    public void diagnosticoConcluido(String descricaoProblema, List<OrdemServicoItemEstoque> pecas,
            List<OrdemServicoItemServico> servicos) {
        this.descricaoProblema = descricaoProblema;
        diagnosticoConcluido(pecas, servicos);
    }

    public boolean todasPecasDisponiveis() {
        if (this.pecas.isEmpty()) {
            return true;
        }
        return this.pecas.stream().allMatch(p -> Boolean.TRUE.equals(p.getDisponivel()));
    }

    public Double calcularValorTotal() {
        double totalPecas = this.pecas.stream()
                .filter(p -> p.getQuantidade() != null && p.getValorUnitario() != null)
                .mapToDouble(OrdemServicoItemEstoque::getValorTotal)
                .sum();

        double totalServicos = this.servicos.stream()
                .filter(s -> s.getValorAplicado() != null)
                .mapToDouble(OrdemServicoItemServico::getValorAplicado)
                .sum();

        this.valorTotal = totalPecas + totalServicos;
        return this.valorTotal;
    }

    public Long getId() {
        return id;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public StatusOrdemServico getStatus() {
        return status;
    }

    public String getDescricaoProblema() {
        return descricaoProblema;
    }

    public void setDescricaoProblema(String descricaoProblema) {
        this.descricaoProblema = descricaoProblema;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public LocalDateTime getDataHoraCriacao() {
        return dataHoraCriacao;
    }

    public LocalDateTime getDataHoraAtualizacao() {
        return dataHoraAtualizacao;
    }

    public List<OrdemServicoItemEstoque> getPecas() {
        return Collections.unmodifiableList(pecas);
    }

    public List<OrdemServicoItemServico> getServicos() {
        return Collections.unmodifiableList(servicos);
    }
}
