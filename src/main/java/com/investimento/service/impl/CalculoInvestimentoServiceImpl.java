package com.investimento.service.impl;

import com.investimento.api.dto.ResultadoSimulacaoResponse;
import com.investimento.service.CalculoInvestimentoService;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@ApplicationScoped
public class CalculoInvestimentoServiceImpl implements CalculoInvestimentoService {

    private static final MathContext MATH_CONTEXT = new MathContext(12, RoundingMode.HALF_UP);

    @Override
    public ResultadoSimulacaoResponse calcular(BigDecimal valorInicial, Integer prazoMeses, BigDecimal rentabilidadeAnual) {
        BigDecimal taxaMensal = rentabilidadeAnual.divide(BigDecimal.valueOf(12), MATH_CONTEXT);
        BigDecimal fator = BigDecimal.ONE.add(taxaMensal).pow(prazoMeses, MATH_CONTEXT);

        BigDecimal valorFinal = valorInicial.multiply(fator, MATH_CONTEXT).setScale(2, RoundingMode.HALF_UP);
        BigDecimal rentabilidadeEfetiva = valorFinal
                .divide(valorInicial, MATH_CONTEXT)
                .subtract(BigDecimal.ONE)
                .setScale(6, RoundingMode.HALF_UP);

        return new ResultadoSimulacaoResponse(valorFinal, rentabilidadeEfetiva, prazoMeses);
    }
}
