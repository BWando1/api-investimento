package com.investimento.service;

import com.investimento.api.dto.SimularInvestimentoRequest;
import com.investimento.domain.entity.Produto;

public interface ProdutoService {

    Produto selecionarProdutoElegivel(SimularInvestimentoRequest request);
}
