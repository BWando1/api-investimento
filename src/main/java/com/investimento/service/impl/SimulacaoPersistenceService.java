package com.investimento.service.impl;

import com.investimento.domain.entity.Produto;
import com.investimento.domain.entity.Simulacao;
import com.investimento.repository.ProdutoRepository;
import com.investimento.repository.SimulacaoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class SimulacaoPersistenceService {

    @Inject
    SimulacaoRepository simulacaoRepository;

    @Inject
    ProdutoRepository produtoRepository;

    @Transactional
    public void salvar(Simulacao simulacao, Long produtoId) {
        Produto produto = produtoRepository.getReferenceById(produtoId);
        simulacao.produto = produto;
        simulacaoRepository.persist(simulacao);
    }
}