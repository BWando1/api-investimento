package com.investimento.api.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record SimulacaoHistoricoResponse(
        Long id,
        Long clienteId,
        String produto,
        BigDecimal valorInvestido,
        BigDecimal valorFinal,
        Integer prazoMeses,
        OffsetDateTime dataSimulacao
) {
}
