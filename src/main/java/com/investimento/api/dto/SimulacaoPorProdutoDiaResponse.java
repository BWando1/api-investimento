package com.investimento.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SimulacaoPorProdutoDiaResponse(
        String produto,
        LocalDate data,
        Long quantidadeSimulacoes,
        BigDecimal mediaValorFinal
) {
}
