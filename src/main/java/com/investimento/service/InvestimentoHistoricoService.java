package com.investimento.service;

import com.investimento.api.dto.InvestimentoHistoricoResponse;

import java.util.List;

public interface InvestimentoHistoricoService {

    List<InvestimentoHistoricoResponse> listarPorCliente(Long clienteId);
}
