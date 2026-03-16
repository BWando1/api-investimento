package com.investimento.api.dto;

public record PerfilRiscoResponse(
        Long clienteId,
        String perfil,
        Integer pontuacao,
        String descricao
) {
}
