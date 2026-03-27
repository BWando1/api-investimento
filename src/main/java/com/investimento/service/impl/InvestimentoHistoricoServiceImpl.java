package com.investimento.service.impl;

import com.investimento.api.dto.InvestimentoHistoricoResponse;
import com.investimento.repository.InvestimentoHistoricoRepository;
import com.investimento.service.InvestimentoHistoricoService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class InvestimentoHistoricoServiceImpl implements InvestimentoHistoricoService {

    @Inject
    InvestimentoHistoricoRepository investimentoHistoricoRepository;

    @Override
    public List<InvestimentoHistoricoResponse> listarPorCliente(Long clienteId) {
        return investimentoHistoricoRepository.findByClienteId(clienteId).stream()
                .map(i -> new InvestimentoHistoricoResponse(
                        i.id,
                        i.tipo,
                        i.valor,
                        i.rentabilidade,
                        i.data
                ))
                .toList();
    }
}
