package com.investimento.api.mapper;

import com.investimento.api.dto.InvestimentoHistoricoResponse;
import com.investimento.domain.entity.InvestimentoHistorico;

import java.util.List;

public final class InvestimentoHistoricoMapper {

    private InvestimentoHistoricoMapper() {}

    public static InvestimentoHistoricoResponse toResponse(InvestimentoHistorico entity) {
        return new InvestimentoHistoricoResponse(
                entity.id,
                entity.tipo,
                entity.valor,
                entity.rentabilidade,
                entity.data
        );
    }

    public static List<InvestimentoHistoricoResponse> toResponseList(List<InvestimentoHistorico> entities) {
        return entities.stream()
                .map(InvestimentoHistoricoMapper::toResponse)
                .toList();
    }
}
