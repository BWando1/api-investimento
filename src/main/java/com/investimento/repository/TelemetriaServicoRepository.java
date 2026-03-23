package com.investimento.repository;

import com.investimento.domain.entity.TelemetriaServico;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TelemetriaServicoRepository implements PanacheRepository<TelemetriaServico> {

	public Optional<TelemetriaServico> findByNomeServico(String nomeServico) {
		return find("nomeServico", nomeServico).firstResultOptional();
	}

	public OffsetDateTime menorAtualizacao() {
		return getEntityManager()
				.createQuery("select min(t.ultimaAtualizacao) from TelemetriaServico t", OffsetDateTime.class)
				.getSingleResult();
	}

	public OffsetDateTime maiorAtualizacao() {
		return getEntityManager()
				.createQuery("select max(t.ultimaAtualizacao) from TelemetriaServico t", OffsetDateTime.class)
				.getSingleResult();
	}

	public List<TelemetriaServico> listarOrdenadoPorNome() {
		return list("order by nomeServico asc");
	}
}
