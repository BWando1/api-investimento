package com.investimento.service.impl;

import com.investimento.api.dto.PageResponse;
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
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SimulacaoServiceImplTest {

    @Test
    void shouldSimulateAndPersist() throws Exception {
        ProdutoService produtoService = mock(ProdutoService.class);
        CalculoInvestimentoService calculoService = mock(CalculoInvestimentoService.class);
        SimulacaoRepository repository = mock(SimulacaoRepository.class);

        Produto produto = new Produto();
        produto.id = 101L;
        produto.nome = "CDB Caixa 2026";
        produto.tipo = "CDB";
        produto.risco = "Baixo";
        produto.rentabilidade = new BigDecimal("0.12");

        SimularInvestimentoRequest request = new SimularInvestimentoRequest(
                999L,
                new BigDecimal("10000.00"),
                12,
                "CDB"
        );

        ResultadoSimulacaoResponse calculo = new ResultadoSimulacaoResponse(
                new BigDecimal("11268.25"),
                new BigDecimal("0.126825"),
                12
        );

        when(produtoService.selecionarProdutoElegivel(request)).thenReturn(produto);
        when(calculoService.calcular(request.valor(), request.prazoMeses(), produto.rentabilidade)).thenReturn(calculo);

        SimulacaoServiceImpl service = new SimulacaoServiceImpl();
        inject(service, "produtoService", produtoService);
        inject(service, "calculoInvestimentoService", calculoService);
        inject(service, "simulacaoRepository", repository);

        SimularInvestimentoResponse response = service.simular(request);

        ArgumentCaptor<Simulacao> captor = ArgumentCaptor.forClass(Simulacao.class);
        verify(repository).persist(captor.capture());
        Simulacao persisted = captor.getValue();

        assertEquals(999L, persisted.clienteId);
        assertEquals("CDB", persisted.tipoProduto);
        assertEquals(new BigDecimal("11268.25"), persisted.valorFinal);

        assertEquals("CDB Caixa 2026", response.produtoValidado().nome());
        assertEquals(new BigDecimal("11268.25"), response.resultadoSimulacao().valorFinal());
    }

    @Test
    void shouldListHistoryPaginated() throws Exception {
        SimulacaoRepository repository = mock(SimulacaoRepository.class);
        PanacheQuery<Simulacao> query = mock(PanacheQuery.class);

        Simulacao simulacao = new Simulacao();
        simulacao.id = 1L;
        simulacao.clienteId = 999L;
        simulacao.produtoNome = "CDB Caixa 2026";
        simulacao.valorInvestido = new BigDecimal("10000.00");
        simulacao.valorFinal = new BigDecimal("11268.25");
        simulacao.prazoMeses = 12;
        simulacao.dataSimulacao = OffsetDateTime.now();

        when(repository.find("ORDER BY dataSimulacao DESC")).thenReturn(query);
        when(query.count()).thenReturn(1L);
        when(query.page(any(Page.class))).thenReturn(query);
        when(query.list()).thenReturn(List.of(simulacao));

        SimulacaoServiceImpl service = new SimulacaoServiceImpl();
        inject(service, "simulacaoRepository", repository);

        PageResponse<SimulacaoHistoricoResponse> page = service.listarSimulacoes(0, 10);

        assertEquals(1L, page.totalElements());
        assertEquals(1, page.content().size());
        assertEquals("CDB Caixa 2026", page.content().getFirst().produto());
    }

    @Test
    void shouldDelegateGroupedListingToRepository() throws Exception {
        SimulacaoRepository repository = mock(SimulacaoRepository.class);
        List<SimulacaoPorProdutoDiaResponse> esperado = List.of(
                new SimulacaoPorProdutoDiaResponse("CDB Caixa 2026", LocalDate.now(), 2L, new BigDecimal("11300.00"))
        );
        when(repository.listarAgregadoPorProdutoDia()).thenReturn(esperado);

        SimulacaoServiceImpl service = new SimulacaoServiceImpl();
        inject(service, "simulacaoRepository", repository);

        List<SimulacaoPorProdutoDiaResponse> atual = service.listarPorProdutoDia();

        assertEquals(esperado, atual);
        verify(repository).listarAgregadoPorProdutoDia();
    }

    private void inject(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
