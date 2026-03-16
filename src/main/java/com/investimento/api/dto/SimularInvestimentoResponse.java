package com.investimento.api.dto;

import java.time.OffsetDateTime;

public record SimularInvestimentoResponse(
        ProdutoResumoResponse produtoValidado,
        ResultadoSimulacaoResponse resultadoSimulacao,
        OffsetDateTime dataSimulacao
) {
}
