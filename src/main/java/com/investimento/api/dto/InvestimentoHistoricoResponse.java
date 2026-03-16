package com.investimento.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvestimentoHistoricoResponse(
        Long id,
        String tipo,
        BigDecimal valor,
        BigDecimal rentabilidade,
        LocalDate data
) {
}
