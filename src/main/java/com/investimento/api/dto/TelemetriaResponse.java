package com.investimento.api.dto;

import java.util.List;

public record TelemetriaResponse(
        List<TelemetriaServicoResponse> servicos,
        TelemetriaPeriodoResponse periodo
) {
}
