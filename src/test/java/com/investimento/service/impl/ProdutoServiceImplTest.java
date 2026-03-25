package com.investimento.service.impl;

import com.investimento.api.dto.SimularInvestimentoRequest;
import com.investimento.api.exception.ResourceNotFoundException;
import com.investimento.domain.entity.Produto;
import com.investimento.repository.ProdutoRepository;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProdutoServiceImplTest {

    @Test
    void shouldReturnFirstEligibleProduct() throws Exception {
        ProdutoRepository repository = mock(ProdutoRepository.class);
        SimularInvestimentoRequest request = new SimularInvestimentoRequest(
                999L,
                new BigDecimal("10000.00"),
                12,
                "CDB"
        );

        Produto produto = new Produto();
        produto.id = 101L;
        produto.nome = "CDB Caixa 2026";

        when(repository.buscarElegiveis("CDB", new BigDecimal("10000.00"), 12))
                .thenReturn(List.of(produto));

        ProdutoServiceImpl service = new ProdutoServiceImpl();
        inject(service, "produtoRepository", repository);

        Produto resultado = service.selecionarProdutoElegivel(request);

        assertEquals(101L, resultado.id);
        assertEquals("CDB Caixa 2026", resultado.nome);
    }

    @Test
    void shouldThrowWhenNoEligibleProductFound() throws Exception {
        ProdutoRepository repository = mock(ProdutoRepository.class);
        SimularInvestimentoRequest request = new SimularInvestimentoRequest(
                999L,
                new BigDecimal("100.00"),
                1,
                "CDB"
        );

        when(repository.buscarElegiveis("CDB", new BigDecimal("100.00"), 1))
                .thenReturn(List.of());

        ProdutoServiceImpl service = new ProdutoServiceImpl();
        inject(service, "produtoRepository", repository);

        assertThrows(ResourceNotFoundException.class, () -> service.selecionarProdutoElegivel(request));
    }

    private void inject(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
