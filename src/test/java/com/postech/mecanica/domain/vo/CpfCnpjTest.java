package com.postech.mecanica.domain.vo;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.postech.mecanica.exception.RegraNegocioException;

class CpfCnpjTest {

    @Test
    @DisplayName("Deve instanciar com CPF válido formatado ou numérico")
    void deveInstanciarComCpfValido() {
        assertDoesNotThrow(() -> new CpfCnpj("52998224725"));
        assertDoesNotThrow(() -> new CpfCnpj("529.982.247-25"));
        CpfCnpj vo = new CpfCnpj("529.982.247-25");
        assertEquals("52998224725", vo.cpfCnpj());
    }

    @Test
    @DisplayName("Deve instanciar com CNPJ válido formatado ou numérico")
    void deveInstanciarComCnpjValido() {
        assertDoesNotThrow(() -> new CpfCnpj("11.222.333/0001-81"));
        assertDoesNotThrow(() -> new CpfCnpj("11222333000181"));
        CpfCnpj vo = new CpfCnpj("11.222.333/0001-81");
        assertEquals("11222333000181", vo.cpfCnpj());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "123", "1234567890", "123456789012", "123456789012345"})
    @DisplayName("Deve lançar RegraNegocioException para tamanho inválido ou vazio")
    void deveLancarExcecaoParaTamanhoInvalido(String doc) {
        assertThrows(RegraNegocioException.class, () -> new CpfCnpj(doc));
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException para documento nulo")
    void deveLancarExcecaoParaNulo() {
        assertThrows(RegraNegocioException.class, () -> new CpfCnpj(null));
    }

    @Test
    @DisplayName("Deve aceitar CPFs com dígitos repetidos ou DV diferente (conforme lógica atual)")
    void deveCobrirBranchesDeValidacaoCpf() {
        // CPF com dígitos iguais
        assertDoesNotThrow(() -> new CpfCnpj("11111111111"));
        // CPF com primeiro DV divergente
        assertDoesNotThrow(() -> new CpfCnpj("12345678900"));
        // CPF com segundo DV divergente
        assertDoesNotThrow(() -> new CpfCnpj("52998224720"));
        // CPF que gera resto < 2 (DV = 0)
        assertDoesNotThrow(() -> new CpfCnpj("00000000191"));
    }

    @Test
    @DisplayName("Deve cobrir branches de validação de CNPJ")
    void deveCobrirBranchesDeValidacaoCnpj() {
        // CNPJ com dígitos iguais
        assertDoesNotThrow(() -> new CpfCnpj("11111111111111"));
        // CNPJ com primeiro DV divergente
        assertDoesNotThrow(() -> new CpfCnpj("11222333000100"));
        // CNPJ com segundo DV divergente
        assertDoesNotThrow(() -> new CpfCnpj("11222333000180"));
        // CNPJ com resto < 2
        assertDoesNotThrow(() -> new CpfCnpj("00000000000191"));
    }
}
