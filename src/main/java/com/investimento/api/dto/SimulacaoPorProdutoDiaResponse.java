package com.investimento.api.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public record SimulacaoPorProdutoDiaResponse(
        String produto,
        LocalDate data,
        Long quantidadeSimulacoes,
        BigDecimal mediaValorFinal
) {
    public SimulacaoPorProdutoDiaResponse {
        if (mediaValorFinal != null) {
            mediaValorFinal = mediaValorFinal.setScale(2, RoundingMode.HALF_UP);
        }
    }
}
