package com.investimento.service.impl;

import com.investimento.domain.entity.Produto;
import com.investimento.domain.entity.Simulacao;
import com.investimento.repository.ProdutoRepository;
import com.investimento.repository.SimulacaoRepository;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SimulacaoPersistenceServiceTest {

    @Test
    void shouldAttachManagedProductReferenceBeforePersist() throws Exception {
        ProdutoRepository produtoRepository = mock(ProdutoRepository.class);
        SimulacaoRepository simulacaoRepository = mock(SimulacaoRepository.class);

        Produto produto = new Produto();
        produto.id = 101L;

        Simulacao simulacao = new Simulacao();
        simulacao.clienteId = 999L;
        simulacao.produtoNome = "CDB Caixa 2026";
        simulacao.tipoProduto = "CDB";
        simulacao.valorInvestido = new BigDecimal("10000.00");
        simulacao.valorFinal = new BigDecimal("11268.25");
        simulacao.rentabilidadeEfetiva = new BigDecimal("0.126825");
        simulacao.prazoMeses = 12;
        simulacao.dataSimulacao = OffsetDateTime.now();

        when(produtoRepository.findById(101L)).thenReturn(produto);

        SimulacaoPersistenceService service = new SimulacaoPersistenceService();
        inject(service, "produtoRepository", produtoRepository);
        inject(service, "simulacaoRepository", simulacaoRepository);

        service.salvar(simulacao, 101L);

        assertSame(produto, simulacao.produto);
        verify(simulacaoRepository).persist(simulacao);
    }

    private void inject(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}