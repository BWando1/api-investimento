package com.investimento.service;

import com.investimento.api.dto.ProdutoResumoResponse;

import java.util.List;

public interface ProdutoRecomendadoService {

    List<ProdutoResumoResponse> listarPorPerfil(String perfilParam);
}
