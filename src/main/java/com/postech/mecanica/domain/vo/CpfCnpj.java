package com.postech.mecanica.domain.vo;

import com.postech.mecanica.exception.RegraNegocioException;

public record CpfCnpj(String cpfCnpj) {
    public CpfCnpj {
        if (cpfCnpj == null || cpfCnpj.isBlank()) {
            throw new RegraNegocioException("Cpf - Cnpj não pode ser vazio");
        }

        // Deixa apenas os números
        cpfCnpj = cpfCnpj.replaceAll("\\D", "");
        if (cpfCnpj.length() == 11) {
            validarCpf(cpfCnpj);
        } else if (cpfCnpj.length() == 14) {
            validarCnpj(cpfCnpj);
        } else {
            throw new RegraNegocioException("Documento deve ter 11 dígitos (CPF) ou 14 dígitos (CNPJ).");
        }
    }

    private boolean validarCpf(String cpf) {
        if (cpf.chars().distinct().count() == 1) {
            return false; // ex: 111.111.111-11
        }

        int[] nums = cpf.chars().map(c -> c - '0').toArray();

        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += nums[i] * (10 - i);
        }
        int resto = soma % 11;
        int dv1 = (resto < 2) ? 0 : 11 - resto;
        if (dv1 != nums[9]) {
            return false;
        }

        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += nums[i] * (11 - i);
        }
        resto = soma % 11;
        int dv2 = (resto < 2) ? 0 : 11 - resto;
        return dv2 == nums[10];
    }

    private boolean validarCnpj(String cnpj) {
        if (cnpj.chars().distinct().count() == 1) {
            return false;
        }

        int[] nums = cnpj.chars().map(c -> c - '0').toArray();

        int[] pesos1 = { 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2 };
        int soma = 0;
        for (int i = 0; i < 12; i++) {
            soma += nums[i] * pesos1[i];
        }
        int resto = soma % 11;
        int dv1 = (resto < 2) ? 0 : 11 - resto;
        if (dv1 != nums[12]) {
            return false;
        }

        int[] pesos2 = { 6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2 };
        soma = 0;
        for (int i = 0; i < 13; i++) {
            soma += nums[i] * pesos2[i];
        }
        resto = soma % 11;
        int dv2 = (resto < 2) ? 0 : 11 - resto;
        return dv2 == nums[13];
    }
}
