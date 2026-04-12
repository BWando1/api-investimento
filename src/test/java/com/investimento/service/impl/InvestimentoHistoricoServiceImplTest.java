package com.investimento.service.impl;

import com.investimento.api.dto.InvestimentoHistoricoResponse;
import com.investimento.domain.entity.InvestimentoHistorico;
import com.investimento.domain.entity.Produto;
import com.investimento.repository.InvestimentoHistoricoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InvestimentoHistoricoServiceImplTest {

    private InvestimentoHistoricoServiceImpl service;
    private InvestimentoHistoricoRepository repository;

    @BeforeEach
    void setUp() throws Exception {
        service = new InvestimentoHistoricoServiceImpl();
        repository = mock(InvestimentoHistoricoRepository.class);

        // Inject mock
        Field field = InvestimentoHistoricoServiceImpl.class.getDeclaredField("investimentoHistoricoRepository");
        field.setAccessible(true);
        field.set(service, repository);
    }

    @Test
    void shouldListarPorClienteWithResults() {
        // Arrange
        Long clienteId = 123L;

        Produto produto = new Produto();
        produto.id = 101L;
        produto.nome = "CDB Test";

        InvestimentoHistorico h1 = new InvestimentoHistorico();
        h1.id = 1L;
        h1.tipo = "CDB";
        h1.valor = new BigDecimal("5000.00");
        h1.rentabilidade = new BigDecimal("0.12");
        h1.data = LocalDate.now();
        h1.produto = produto;

        InvestimentoHistorico h2 = new InvestimentoHistorico();
        h2.id = 2L;
        h2.tipo = "LCI";
        h2.valor = new BigDecimal("10000.00");
        h2.rentabilidade = new BigDecimal("0.10");
        h2.data = LocalDate.now().minusDays(5);
        h2.produto = produto;

        when(repository.findByClienteId(clienteId)).thenReturn(List.of(h1, h2));

        // Act
        List<InvestimentoHistoricoResponse> result = service.listarPorCliente(clienteId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(1L, result.get(0).id());
        assertEquals("CDB", result.get(0).tipo());
        assertEquals(new BigDecimal("5000.00"), result.get(0).valor());
        assertEquals(new BigDecimal("0.12"), result.get(0).rentabilidade());
        assertEquals(LocalDate.now(), result.get(0).data());

        assertEquals(2L, result.get(1).id());
        assertEquals("LCI", result.get(1).tipo());
    }

    @Test
    void shouldListarPorClienteWithEmptyResults() {
        // Arrange
        Long clienteId = 999L;
        when(repository.findByClienteId(clienteId)).thenReturn(List.of());

        // Act
        List<InvestimentoHistoricoResponse> result = service.listarPorCliente(clienteId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository).findByClienteId(clienteId);
    }

    @Test
    void shouldMapAllFieldsCorrectly() {
        // Arrange
        Long clienteId = 100L;

        InvestimentoHistorico historico = new InvestimentoHistorico();
        historico.id = 42L;
        historico.tipo = "TESOURO_DIRETO";
        historico.valor = new BigDecimal("25000.50");
        historico.rentabilidade = new BigDecimal("0.085");
        historico.data = LocalDate.of(2026, 3, 15);

        when(repository.findByClienteId(clienteId)).thenReturn(List.of(historico));

        // Act
        List<InvestimentoHistoricoResponse> result = service.listarPorCliente(clienteId);

        // Assert
        assertEquals(1, result.size());
        InvestimentoHistoricoResponse response = result.get(0);

        assertEquals(42L, response.id());
        assertEquals("TESOURO_DIRETO", response.tipo());
        assertEquals(new BigDecimal("25000.50"), response.valor());
        assertEquals(new BigDecimal("0.085"), response.rentabilidade());
        assertEquals(LocalDate.of(2026, 3, 15), response.data());
    }
}
