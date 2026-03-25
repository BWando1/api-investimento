package com.investimento.service.impl;

import com.investimento.api.dto.ProdutoResumoResponse;
import com.investimento.api.exception.BusinessException;
import com.investimento.domain.entity.Produto;
import com.investimento.repository.ProdutoRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProdutoRecomendadoServiceImplTest {

    @Test
    void shouldMapProductsForModerateProfile() throws Exception {
        ProdutoRepository repository = mock(ProdutoRepository.class);
        Produto produto = new Produto();
        produto.id = 101L;
        produto.nome = "CDB Caixa 2026";
        produto.tipo = "CDB";
        produto.rentabilidade = new BigDecimal("0.12");
        produto.risco = "Baixo";

        when(repository.buscarRecomendadosPorRiscos(List.of("Baixo", "Medio"))).thenReturn(List.of(produto));

        ProdutoRecomendadoServiceImpl service = new ProdutoRecomendadoServiceImpl();
        inject(service, "produtoRepository", repository);

        List<ProdutoResumoResponse> result = service.listarPorPerfil("moderado");

        ArgumentCaptor<List<String>> captor = ArgumentCaptor.forClass(List.class);
        verify(repository).buscarRecomendadosPorRiscos(captor.capture());
        assertEquals(List.of("Baixo", "Medio"), captor.getValue());
        assertEquals(1, result.size());
        assertEquals("CDB Caixa 2026", result.getFirst().nome());
    }

    @Test
    void shouldThrowBusinessExceptionForInvalidProfile() throws Exception {
        ProdutoRecomendadoServiceImpl service = new ProdutoRecomendadoServiceImpl();
        inject(service, "produtoRepository", mock(ProdutoRepository.class));

        BusinessException ex = assertThrows(BusinessException.class, () -> service.listarPorPerfil("xpto"));
        assertEquals(400, ex.getStatusCode());
    }

    private void inject(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
