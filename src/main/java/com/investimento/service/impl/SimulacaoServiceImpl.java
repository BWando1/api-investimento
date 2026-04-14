package com.investimento.service.impl;

import com.investimento.api.dto.PageResponse;
import com.investimento.api.dto.ProdutoResumoResponse;
import com.investimento.api.dto.ResultadoSimulacaoResponse;
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

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@ApplicationScoped
public class SimulacaoServiceImpl implements SimulacaoService {

    @Inject
    ProdutoService produtoService;

    @Inject
    CalculoInvestimentoService calculoInvestimentoService;

    @Inject
    SimulacaoRepository simulacaoRepository;

    @Inject
    SimulacaoPersistenceService simulacaoPersistenceService;

    @Override
    public SimularInvestimentoResponse simular(SimularInvestimentoRequest request) {

        Produto produto = produtoService.selecionarProdutoElegivel(request);
        
        ResultadoSimulacaoResponse calculo = calculoInvestimentoService
                .calcular(request.valor(), request.prazoMeses(), produto.rentabilidade);

        
        OffsetDateTime agora = OffsetDateTime.now();
        Simulacao simulacao = new Simulacao();
        simulacao.clienteId = request.clienteId();
        simulacao.produto = produto;
        simulacao.produtoNome = produto.nome;
        simulacao.tipoProduto = produto.tipo;
        simulacao.valorInvestido = request.valor();
        simulacao.valorFinal = calculo.valorFinal();
        simulacao.rentabilidadeEfetiva = calculo.rentabilidadeEfetiva();
        simulacao.prazoMeses = request.prazoMeses();
        simulacao.dataSimulacao = agora;
        simulacao.dataSimulacaoDate = agora.toLocalDate();

        simulacaoPersistenceService.salvar(simulacao, produto.id);

        ProdutoResumoResponse produtoResumo = new ProdutoResumoResponse(
                produto.id,
                produto.nome,
                produto.tipo,
                produto.rentabilidade,
                produto.risco
        );

        return new SimularInvestimentoResponse(produtoResumo, calculo, agora);
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
        return simulacaoRepository.listarAgregadoPorProdutoDia();
    }
}
