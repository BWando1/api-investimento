package com.investimento.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SimularInvestimentoRequest(
        @NotNull Long clienteId,
        @NotNull @DecimalMin("0.01") BigDecimal valor,
        @NotNull @Min(1) Integer prazoMeses,
        @NotBlank String tipoProduto
) {
}
