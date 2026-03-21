package com.investimento.repository;

import com.investimento.domain.entity.Produto;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class ProdutoRepository implements PanacheRepositoryBase<Produto, Long> {

	public List<Produto> buscarElegiveis(String tipoProduto, BigDecimal valor, Integer prazoMeses) {
		return list(
				"ativo = true AND UPPER(tipo) = UPPER(?1) AND valorMinimo <= ?2 AND prazoMinMeses <= ?3 AND prazoMaxMeses >= ?3 ORDER BY rentabilidade DESC",
				tipoProduto,
				valor,
				prazoMeses
		);
	}
}
