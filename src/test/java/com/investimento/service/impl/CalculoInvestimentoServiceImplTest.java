package com.investimento.service.impl;

import com.investimento.api.dto.ResultadoSimulacaoResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculoInvestimentoServiceImplTest {

    private final CalculoInvestimentoServiceImpl service = new CalculoInvestimentoServiceImpl();

    @Test
    void shouldCalculateCompoundInterest() {
        ResultadoSimulacaoResponse response = service.calcular(
                new BigDecimal("10000.00"),
                12,
                new BigDecimal("0.12")
        );

        assertEquals(new BigDecimal("11268.25"), response.valorFinal());
        assertEquals(new BigDecimal("0.126825"), response.rentabilidadeEfetiva());
        assertEquals(12, response.prazoMeses());
    }
}
