package com.investimento.service.impl;

import com.investimento.api.dto.SimularInvestimentoRequest;
import com.investimento.api.exception.ResourceNotFoundException;
import com.investimento.domain.entity.Produto;
import com.investimento.repository.ProdutoRepository;
import com.investimento.service.ProdutoService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class ProdutoServiceImpl implements ProdutoService {

    @Inject
    ProdutoRepository produtoRepository;

    @Override
    public Produto selecionarProdutoElegivel(SimularInvestimentoRequest request) {
        List<Produto> elegiveis = produtoRepository.buscarElegiveis(
                request.tipoProduto(),
                request.valor(),
                request.prazoMeses()
        );

        if (elegiveis.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Nenhum produto elegivel encontrado para tipo '" + request.tipoProduto() +
                            "', valor " + request.valor() + " e prazo " + request.prazoMeses() + " meses.");
        }

        return elegiveis.get(0);
    }
}
