package com.investimento.repository;

import com.investimento.domain.entity.TelemetriaServico;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TelemetriaServicoRepository implements PanacheRepository<TelemetriaServico> {
}
