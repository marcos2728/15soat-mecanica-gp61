package com.postech.mecanica.domain.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.postech.mecanica.domain.vo.CpfCnpj;
import com.postech.mecanica.exception.RegraNegocioException;

public class Cliente {

    private Long id;
    private CpfCnpj cpfCnpj;
    private String nome;
    private String email;
    private String telefone;
    private String dataDeNascimento;
    private final List<Veiculo> veiculos = new ArrayList<>();

    public Cliente(Long id, CpfCnpj cpfCnpj, String nome, String email, String telefone, String dataDeNascimento,
            List<Veiculo> veiculos) {
        validarDadosObrigatorios(cpfCnpj, nome);
        this.id = id;
        this.cpfCnpj = cpfCnpj;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.dataDeNascimento = dataDeNascimento;
        if (veiculos != null) {
            this.veiculos.addAll(veiculos);
        }
    }

    public Cliente(Long id, CpfCnpj cpfCnpj, String nome, String email, String telefone, String dataDeNascimento,
            Veiculo veiculo) {
        this(id, cpfCnpj, nome, email, telefone, dataDeNascimento, veiculo != null ? List.of(veiculo) : List.of());
    }

    private void validarDadosObrigatorios(CpfCnpj cpfCnpj, String nome) {
        if (cpfCnpj == null) {
            throw new RegraNegocioException("O cliente precisa de um CPF ou CNPJ válido.");
        }
        if (nome == null || nome.isBlank()) {
            throw new RegraNegocioException("O nome do cliente é obrigatório.");
        }
    }

    public void atualizarDados(String nome, String dataDeNascimento, String email, String telefone) {
        if (nome != null && !nome.isBlank()) {
            this.nome = nome;
        }
        if (dataDeNascimento != null && !dataDeNascimento.isBlank()) {
            this.dataDeNascimento = dataDeNascimento;
        }
        if (email != null && !email.isBlank()) {
            this.email = email;
        }
        if (telefone != null && !telefone.isBlank()) {
            this.telefone = telefone;
        }
    }

    public void adicionarVeiculo(Veiculo novoVeiculo) {
        if (novoVeiculo == null) {
            throw new RegraNegocioException("Veículo não pode ser nulo.");
        }
        boolean jaExiste = this.veiculos.stream()
                .anyMatch(v -> v.getPlaca() != null && novoVeiculo.getPlaca() != null
                        && v.getPlaca().placa().equalsIgnoreCase(novoVeiculo.getPlaca().placa()));
        if (jaExiste) {
            throw new RegraNegocioException("Este veículo já está associado a este cliente.");
        }
        this.veiculos.add(novoVeiculo);
    }

    public void associarVeiculo(Veiculo novoVeiculo) {
        adicionarVeiculo(novoVeiculo);
    }

    public void removerVeiculo(Veiculo veiculo) {
        this.veiculos.remove(veiculo);
    }

    public Long getId() {
        return id;
    }

    public CpfCnpj getCpfCnpj() {
        return cpfCnpj;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getDataDeNascimento() {
        return dataDeNascimento;
    }

    public List<Veiculo> getVeiculos() {
        return Collections.unmodifiableList(veiculos);
    }
}
