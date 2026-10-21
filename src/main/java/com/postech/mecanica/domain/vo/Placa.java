package com.postech.mecanica.domain.vo;

import java.util.regex.Pattern;
import com.postech.mecanica.exception.RegraNegocioException;

public record Placa(String placa) {

    // Valida tanto padrão Antigo (ABC1234) quanto Mercosul (ABC1D23)
    private static final Pattern PADRAO_PLACA = Pattern.compile("^[A-Z]{3}[0-9][A-Z0-9][0-9]{2}$");

    public Placa {
        if (placa == null || placa.isBlank()) {
            throw new RegraNegocioException("A placa não pode ser vazia.");
        }

        placa = placa.trim().toUpperCase().replace("-", "");

        if (!PADRAO_PLACA.matcher(placa).matches()) {
            throw new RegraNegocioException(
                    "Placa inválida: " + placa + ". Utilize o formato tradicional (ABC1234) ou Mercosul (ABC1D23).");
        }
    }
}
