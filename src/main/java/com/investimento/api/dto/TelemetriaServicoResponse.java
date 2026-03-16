package com.investimento.api.dto;

public record TelemetriaServicoResponse(
        String nome,
        Long quantidadeChamadas,
        Long mediaTempoRespostaMs
) {
}
