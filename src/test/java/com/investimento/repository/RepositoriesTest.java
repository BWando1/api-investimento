package com.investimento.repository;

import com.investimento.api.dto.SimulacaoPorProdutoDiaResponse;
import com.investimento.domain.entity.InvestimentoHistorico;
import com.investimento.domain.entity.Produto;
import com.investimento.domain.entity.Simulacao;
import com.investimento.domain.entity.TelemetriaServico;
import com.investimento.entity.RequestMetric;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de integração para repositories.
 * Cobertura para queries customizadas e métodos não testados pelos endpoints.
 */
@QuarkusTest
class RepositoriesTest {

    @Inject
    TelemetriaServicoRepository telemetriaServicoRepository;

    @Inject
    ProdutoRepository produtoRepository;

    @Inject
    RequestMetricRepository requestMetricRepository;

    @Inject
    InvestimentoHistoricoRepository investimentoHistoricoRepository;

    @Inject
    SimulacaoRepository simulacaoRepository;

    @Test
    @Transactional
    void telemetriaServicoRepository_shouldFindByNomeServico() {
        // Arrange
        TelemetriaServico telemetria = new TelemetriaServico();
        telemetria.nomeServico = "GET /test";
        telemetria.quantidadeChamadas = 5L;
        telemetria.tempoTotalRespostaMs = 500L;
        telemetria.ultimaAtualizacao = OffsetDateTime.now();
        telemetriaServicoRepository.persist(telemetria);

        // Act
        var result = telemetriaServicoRepository.findByNomeServico("GET /test");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("GET /test", result.get().nomeServico);
        assertEquals(5L, result.get().quantidadeChamadas);
    }

    @Test
    @Transactional
    void telemetriaServicoRepository_shouldReturnEmptyWhenNotFound() {
        // Act
        var result = telemetriaServicoRepository.findByNomeServico("NONEXISTENT");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    @Transactional
    void telemetriaServicoRepository_shouldFindMenorAndMaiorAtualizacao() {
        // Arrange
        TelemetriaServico tel1 = new TelemetriaServico();
        tel1.nomeServico = "Service 1";
        tel1.quantidadeChamadas = 1L;
        tel1.tempoTotalRespostaMs = 100L;
        tel1.ultimaAtualizacao = OffsetDateTime.now().minusDays(2);

        TelemetriaServico tel2 = new TelemetriaServico();
        tel2.nomeServico = "Service 2";
        tel2.quantidadeChamadas = 1L;
        tel2.tempoTotalRespostaMs = 100L;
        tel2.ultimaAtualizacao = OffsetDateTime.now();

        telemetriaServicoRepository.persist(tel1);
        telemetriaServicoRepository.persist(tel2);

        // Act
        OffsetDateTime menor = telemetriaServicoRepository.menorAtualizacao();
        OffsetDateTime maior = telemetriaServicoRepository.maiorAtualizacao();

        // Assert
        assertNotNull(menor);
        assertNotNull(maior);
        assertTrue(menor.isBefore(maior) || menor.isEqual(maior));
    }

    @Test
    @Transactional
    void telemetriaServicoRepository_shouldListOrdenadoPorNome() {
        // Arrange
        TelemetriaServico telZ = createTelemetria("Z Service");
        TelemetriaServico telA = createTelemetria("A Service");
        TelemetriaServico telM = createTelemetria("M Service");

        telemetriaServicoRepository.persist(telZ);
        telemetriaServicoRepository.persist(telA);
        telemetriaServicoRepository.persist(telM);

        // Act
        List<TelemetriaServico> result = telemetriaServicoRepository.listarOrdenadoPorNome();

        // Assert
        assertTrue(result.size() >= 3);
        // Find our test entries
        var testEntries = result.stream()
            .filter(t -> t.nomeServico.endsWith(" Service"))
            .toList();
        assertEquals("A Service", testEntries.get(0).nomeServico);
        assertEquals("M Service", testEntries.get(1).nomeServico);
        assertEquals("Z Service", testEntries.get(2).nomeServico);
    }

    @Test
    @Transactional
    void produtoRepository_shouldBuscarElegiveis() {
        // Act - usa produto do seed (CDB Caixa 2026)
        List<Produto> result = produtoRepository.buscarElegiveis(
            "CDB",
            new BigDecimal("5000.00"),
            12
        );

        // Assert
        assertFalse(result.isEmpty());
        assertEquals("CDB", result.get(0).tipo);
    }

    @Test
    @Transactional
    void produtoRepository_shouldBuscarRecomendadosPorRiscos() {
        // Act
        List<Produto> result = produtoRepository.buscarRecomendadosPorRiscos(
            List.of("Baixo", "Medio")
        );

        // Assert
        assertFalse(result.isEmpty());
    }

    @Test
    @Transactional
    void requestMetricRepository_shouldSalvarBatch() {
        // Arrange
        RequestMetric m1 = new RequestMetric("GET /test1", 100L, OffsetDateTime.now());
        RequestMetric m2 = new RequestMetric("POST /test2", 200L, OffsetDateTime.now());
        List<RequestMetric> batch = List.of(m1, m2);

        // Act
        requestMetricRepository.salvar(batch);

        // Assert
        long count = requestMetricRepository.count();
        assertTrue(count >= 2);
    }

    @Test
    @Transactional
    void investimentoHistoricoRepository_shouldFindByClienteId() {
        // Arrange
        Produto produto = produtoRepository.findById(101L);

        InvestimentoHistorico h1 = new InvestimentoHistorico();
        h1.clienteId = 95555L;
        h1.produto = produto;
        h1.tipo = "CDB";
        h1.valor = new BigDecimal("1000.00");
        h1.rentabilidade = new BigDecimal("0.12");
        h1.data = LocalDate.now().minusDays(5);

        InvestimentoHistorico h2 = new InvestimentoHistorico();
        h2.clienteId = 95555L;
        h2.produto = produto;
        h2.tipo = "CDB";
        h2.valor = new BigDecimal("2000.00");
        h2.rentabilidade = new BigDecimal("0.12");
        h2.data = LocalDate.now().minusDays(1);

        investimentoHistoricoRepository.persist(h1);
        investimentoHistoricoRepository.persist(h2);

        // Act
        List<InvestimentoHistorico> result = investimentoHistoricoRepository.findByClienteId(95555L);

        // Assert
        assertEquals(2, result.size());
        // Deve estar ordenado por data DESC
        assertTrue(result.get(0).data.isAfter(result.get(1).data));
    }


    @Test
    @Transactional
    void investimentoHistoricoRepository_shouldFindByClienteIdSince() {
        // Arrange
        Produto produto = produtoRepository.findById(101L);
        LocalDate cutoffDate = LocalDate.now().minusDays(3);

        InvestimentoHistorico h1 = new InvestimentoHistorico();
        h1.clienteId = 888L;
        h1.produto = produto;
        h1.tipo = "CDB";
        h1.valor = new BigDecimal("1000.00");
        h1.rentabilidade = new BigDecimal("0.12");
        h1.data = cutoffDate.minusDays(1);

        InvestimentoHistorico h2 = new InvestimentoHistorico();
        h2.clienteId = 888L;
        h2.produto = produto;
        h2.tipo = "CDB";
        h2.valor = new BigDecimal("2000.00");
        h2.rentabilidade = new BigDecimal("0.12");
        h2.data = cutoffDate.plusDays(1);

        investimentoHistoricoRepository.persist(h1);
        investimentoHistoricoRepository.persist(h2);

        // Act
        List<InvestimentoHistorico> result = investimentoHistoricoRepository.findByClienteIdSince(888L, cutoffDate);

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.get(0).data.isAfter(cutoffDate) || result.get(0).data.isEqual(cutoffDate));
    }

    private TelemetriaServico createTelemetria(String nome) {
        TelemetriaServico tel = new TelemetriaServico();
        tel.nomeServico = nome;
        tel.quantidadeChamadas = 1L;
        tel.tempoTotalRespostaMs = 100L;
        tel.ultimaAtualizacao = OffsetDateTime.now();
        return tel;
    }
}
