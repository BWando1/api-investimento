package com.investimento.service.impl;

import com.investimento.api.dto.PerfilRiscoTipo;
import com.investimento.api.dto.ProdutoResumoResponse;
import com.investimento.api.exception.BusinessException;
import com.investimento.repository.ProdutoRepository;
import com.investimento.service.ProdutoRecomendadoService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class ProdutoRecomendadoServiceImpl implements ProdutoRecomendadoService {

    @Inject
    ProdutoRepository produtoRepository;

    @Override
    public List<ProdutoResumoResponse> listarPorPerfil(String perfilParam) {
        PerfilRiscoTipo perfil;
        try {
            perfil = PerfilRiscoTipo.valueOf(perfilParam.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Perfil invalido. Use CONSERVADOR, MODERADO ou AGRESSIVO.", 400);
        }

        return produtoRepository.buscarRecomendadosPorRiscos(mapearRiscos(perfil)).stream()
                .map(p -> new ProdutoResumoResponse(p.id, p.nome, p.tipo, p.rentabilidade, p.risco))
                .toList();
    }

    private List<String> mapearRiscos(PerfilRiscoTipo perfil) {
        return switch (perfil) {
            case CONSERVADOR -> List.of("Baixo");
            case MODERADO -> List.of("Baixo", "Medio");
            case AGRESSIVO -> List.of("Medio", "Alto");
        };
    }
}
