package com.investimento.api.mapper;

import com.investimento.api.dto.InvestimentoHistoricoResponse;
import com.investimento.domain.entity.InvestimentoHistorico;
import com.investimento.domain.entity.Produto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InvestimentoHistoricoMapperTest {

    @Test
    void shouldMapEntityToResponse() {
        // Arrange
        Produto produto = new Produto();
        produto.id = 101L;

        InvestimentoHistorico entity = new InvestimentoHistorico();
        entity.id = 42L;
        entity.tipo = "CDB";
        entity.valor = new BigDecimal("10000.00");
        entity.rentabilidade = new BigDecimal("0.12");
        entity.data = LocalDate.of(2026, 4, 10);
        entity.produto = produto;

        // Act
        InvestimentoHistoricoResponse response = InvestimentoHistoricoMapper.toResponse(entity);

        // Assert
        assertNotNull(response);
        assertEquals(42L, response.id());
        assertEquals("CDB", response.tipo());
        assertEquals(new BigDecimal("10000.00"), response.valor());
        assertEquals(new BigDecimal("0.12"), response.rentabilidade());
        assertEquals(LocalDate.of(2026, 4, 10), response.data());
    }

    @Test
    void shouldMapEntityListToResponseList() {
        // Arrange
        InvestimentoHistorico h1 = new InvestimentoHistorico();
        h1.id = 1L;
        h1.tipo = "CDB";
        h1.valor = new BigDecimal("5000.00");
        h1.rentabilidade = new BigDecimal("0.10");
        h1.data = LocalDate.now();

        InvestimentoHistorico h2 = new InvestimentoHistorico();
        h2.id = 2L;
        h2.tipo = "LCI";
        h2.valor = new BigDecimal("15000.00");
        h2.rentabilidade = new BigDecimal("0.08");
        h2.data = LocalDate.now().minusDays(5);

        List<InvestimentoHistorico> entities = List.of(h1, h2);

        // Act
        List<InvestimentoHistoricoResponse> responses = InvestimentoHistoricoMapper.toResponseList(entities);

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());

        assertEquals(1L, responses.get(0).id());
        assertEquals("CDB", responses.get(0).tipo());

        assertEquals(2L, responses.get(1).id());
        assertEquals("LCI", responses.get(1).tipo());
    }

    @Test
    void shouldMapEmptyListToEmptyList() {
        // Arrange
        List<InvestimentoHistorico> entities = List.of();

        // Act
        List<InvestimentoHistoricoResponse> responses = InvestimentoHistoricoMapper.toResponseList(entities);

        // Assert
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }
}
