package com.investimento.repository;

import com.investimento.domain.entity.Produto;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProdutoRepository implements PanacheRepositoryBase<Produto, Long> {
}
