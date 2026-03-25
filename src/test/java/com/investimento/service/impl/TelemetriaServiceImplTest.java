package com.investimento.service.impl;

import com.investimento.api.dto.TelemetriaResponse;
import com.investimento.domain.entity.TelemetriaServico;
import com.investimento.repository.TelemetriaServicoRepository;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TelemetriaServiceImplTest {

    @Test
    void shouldReturnTelemetryWithAverages() throws Exception {
        TelemetriaServicoRepository repository = mock(TelemetriaServicoRepository.class);
        TelemetriaServico servico = new TelemetriaServico();
        servico.nomeServico = "GET /simulacoes";
        servico.quantidadeChamadas = 4L;
        servico.tempoTotalRespostaMs = 200L;
        servico.ultimaAtualizacao = OffsetDateTime.now();

        OffsetDateTime now = OffsetDateTime.now();
        when(repository.listarOrdenadoPorNome()).thenReturn(List.of(servico));
        when(repository.menorAtualizacao()).thenReturn(now.minusDays(1));
        when(repository.maiorAtualizacao()).thenReturn(now);

        TelemetriaServiceImpl service = new TelemetriaServiceImpl();
        inject(service, "telemetriaServicoRepository", repository);

        TelemetriaResponse response = service.obterTelemetria();

        assertEquals(1, response.servicos().size());
        assertEquals(50L, response.servicos().getFirst().mediaTempoRespostaMs());
        assertEquals(now.minusDays(1).toLocalDate(), response.periodo().inicio());
        assertEquals(now.toLocalDate(), response.periodo().fim());
    }

    @Test
    void shouldUpdateExistingTelemetryWithoutPersist() throws Exception {
        TelemetriaServicoRepository repository = mock(TelemetriaServicoRepository.class);
        TelemetriaServico existente = new TelemetriaServico();
        existente.id = 10L;
        existente.nomeServico = "GET /telemetria";
        existente.quantidadeChamadas = 2L;
        existente.tempoTotalRespostaMs = 100L;
        existente.ultimaAtualizacao = OffsetDateTime.now();

        when(repository.findByNomeServico("GET /telemetria")).thenReturn(Optional.of(existente));

        TelemetriaServiceImpl service = new TelemetriaServiceImpl();
        inject(service, "telemetriaServicoRepository", repository);

        service.registrarChamada("GET /telemetria", 40L);

        assertEquals(3L, existente.quantidadeChamadas);
        assertEquals(140L, existente.tempoTotalRespostaMs);
        verify(repository, never()).persist(existente);
    }

    @Test
    void shouldCreateTelemetryWhenServiceDoesNotExist() throws Exception {
        TelemetriaServicoRepository repository = mock(TelemetriaServicoRepository.class);
        when(repository.findByNomeServico("POST /simular-investimento")).thenReturn(Optional.empty());

        TelemetriaServiceImpl service = new TelemetriaServiceImpl();
        inject(service, "telemetriaServicoRepository", repository);

        service.registrarChamada("POST /simular-investimento", -5L);

        verify(repository).persist(org.mockito.ArgumentMatchers.any(TelemetriaServico.class));
    }

    private void inject(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
