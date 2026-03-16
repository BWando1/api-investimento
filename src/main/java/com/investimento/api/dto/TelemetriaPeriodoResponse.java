package com.investimento.api.dto;

import java.time.LocalDate;

public record TelemetriaPeriodoResponse(
        LocalDate inicio,
        LocalDate fim
) {
}
