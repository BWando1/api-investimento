package com.investimento.service;

import com.investimento.api.dto.TelemetriaResponse;

public interface TelemetriaService {

    TelemetriaResponse obterTelemetria();

    void registrarChamada(String nomeServico, long tempoRespostaMs);
}
