package com.investimento.service;

import com.investimento.api.dto.PageResponse;
import com.investimento.api.dto.SimularInvestimentoRequest;
import com.investimento.api.dto.SimularInvestimentoResponse;
import com.investimento.api.dto.SimulacaoHistoricoResponse;
import com.investimento.api.dto.SimulacaoPorProdutoDiaResponse;

import java.util.List;

public interface SimulacaoService {

    SimularInvestimentoResponse simular(SimularInvestimentoRequest request);

    PageResponse<SimulacaoHistoricoResponse> listarSimulacoes(int page, int pageSize);

    List<SimulacaoPorProdutoDiaResponse> listarPorProdutoDia();
}
