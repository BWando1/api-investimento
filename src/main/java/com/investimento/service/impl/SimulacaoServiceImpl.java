package com.investimento.service.impl;

import com.investimento.api.dto.PageResponse;
import com.investimento.api.dto.ProdutoResumoResponse;
import com.investimento.api.dto.SimularInvestimentoRequest;
import com.investimento.api.dto.SimularInvestimentoResponse;
import com.investimento.api.dto.SimulacaoHistoricoResponse;
import com.investimento.api.dto.SimulacaoPorProdutoDiaResponse;
import com.investimento.domain.entity.Produto;
import com.investimento.domain.entity.Simulacao;
import com.investimento.repository.SimulacaoRepository;
import com.investimento.service.CalculoInvestimentoService;
import com.investimento.service.ProdutoService;
import com.investimento.service.SimulacaoService;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class SimulacaoServiceImpl implements SimulacaoService {

    @Inject
    ProdutoService produtoService;

    @Inject
    CalculoInvestimentoService calculoInvestimentoService;

    @Inject
    SimulacaoRepository simulacaoRepository;

    @Override
    @Transactional
    public SimularInvestimentoResponse simular(SimularInvestimentoRequest request) {
       
    }

    @Override
    public PageResponse<SimulacaoHistoricoResponse> listarSimulacoes(int page, int pageSize) {
        PanacheQuery<Simulacao> query = simulacaoRepository.find("ORDER BY dataSimulacao DESC");
        long total = query.count();
        List<SimulacaoHistoricoResponse> content = query
                .page(Page.of(page, pageSize))
                .list()
                .stream()
                .map(s -> new SimulacaoHistoricoResponse(
                        s.id,
                        s.clienteId,
                        s.produtoNome,
                        s.valorInvestido,
                        s.valorFinal,
                        s.prazoMeses,
                        s.dataSimulacao
                ))
                .toList();

        return PageResponse.of(content, page, pageSize, total);
    }

    @Override
    public List<SimulacaoPorProdutoDiaResponse> listarPorProdutoDia() {
        List<Simulacao> simulacoes = simulacaoRepository.listAll();

        record ChaveAgrupamento(String produto, LocalDate data) {}

        Map<ChaveAgrupamento, List<Simulacao>> agrupado = simulacoes.stream()
                .collect(Collectors.groupingBy(s -> new ChaveAgrupamento(s.produtoNome, s.dataSimulacao.toLocalDate())));

        return agrupado.entrySet().stream()
                .map(entry -> {
                    ChaveAgrupamento chave = entry.getKey();
                    List<Simulacao> itens = entry.getValue();

                    BigDecimal soma = itens.stream()
                            .map(s -> s.valorFinal)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal media = soma
                            .divide(BigDecimal.valueOf(itens.size()), 2, RoundingMode.HALF_UP);

                    return new SimulacaoPorProdutoDiaResponse(
                            chave.produto(),
                            chave.data(),
                            (long) itens.size(),
                            media
                    );
                })
                .sorted(Comparator
                        .comparing(SimulacaoPorProdutoDiaResponse::data).reversed()
                        .thenComparing(SimulacaoPorProdutoDiaResponse::produto))
                .toList();
    }
}
