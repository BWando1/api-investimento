package com.investimento.repository;

import com.investimento.domain.entity.InvestimentoHistorico;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class InvestimentoHistoricoRepository implements PanacheRepository<InvestimentoHistorico> {
}
