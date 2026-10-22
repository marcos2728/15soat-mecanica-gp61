package com.postech.mecanica.domain.vo;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.postech.mecanica.exception.RegraNegocioException;

class PlacaTest {

    @ParameterizedTest
    @ValueSource(strings = {"ABC1234", "ABC-1234", "abc1234", "abc-1234", "ABC1D23", "abc1d23", "XYZ9K99"})
    @DisplayName("Deve instanciar placa válida nos formatos tradicional e Mercosul")
    void deveInstanciarPlacaValida(String input) {
        assertDoesNotThrow(() -> new Placa(input));
        Placa placa = new Placa(input);
        assertEquals(input.trim().toUpperCase().replace("-", ""), placa.placa());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "ABC123", "ABC12345", "123ABCD", "AB-1234", "ABCD123"})
    @DisplayName("Deve lançar RegraNegocioException para placa em formato inválido")
    void deveLancarExcecaoParaPlacaInvalida(String input) {
        assertThrows(RegraNegocioException.class, () -> new Placa(input));
    }

    @Test
    @DisplayName("Deve lançar RegraNegocioException quando a placa for nula")
    void deveLancarExcecaoParaPlacaNula() {
        assertThrows(RegraNegocioException.class, () -> new Placa(null));
    }
}
