package com.investimento.repository;

import com.investimento.domain.entity.InvestimentoHistorico;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class InvestimentoHistoricoRepository implements PanacheRepositoryBase<InvestimentoHistorico, Long> {

    public List<InvestimentoHistorico> findByClienteIdSince(Long clienteId, LocalDate since) {
        return list("clienteId = ?1 AND data >= ?2 ORDER BY data DESC", clienteId, since);
    }

    public List<InvestimentoHistorico> findByClienteId(Long clienteId) {
        return list("clienteId = ?1 ORDER BY data DESC", clienteId);
    }
}
