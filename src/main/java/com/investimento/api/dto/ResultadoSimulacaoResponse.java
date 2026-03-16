package com.investimento.api.dto;

import java.math.BigDecimal;

public record ResultadoSimulacaoResponse(
        BigDecimal valorFinal,
        BigDecimal rentabilidadeEfetiva,
        Integer prazoMeses
) {
}
