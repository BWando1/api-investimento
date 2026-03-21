package com.investimento.service;

import com.investimento.api.dto.ResultadoSimulacaoResponse;

import java.math.BigDecimal;

public interface CalculoInvestimentoService {

    ResultadoSimulacaoResponse calcular(BigDecimal valorInicial, Integer prazoMeses, BigDecimal rentabilidadeAnual);
}
