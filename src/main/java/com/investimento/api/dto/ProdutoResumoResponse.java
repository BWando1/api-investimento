package com.investimento.api.dto;

import java.math.BigDecimal;

public record ProdutoResumoResponse(
        Long id,
        String nome,
        String tipo,
        BigDecimal rentabilidade,
        String risco
) {
}
