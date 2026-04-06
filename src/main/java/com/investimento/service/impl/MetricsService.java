package com.investimento.service.impl;

import com.investimento.entity.RequestMetric;
import com.investimento.repository.RequestMetricRepository;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Serviço responsável por coletar e persistir métricas de requisição (RequestMetric).
 * <p>
 * - Métricas são enfileiradas via {@link #enqueue(RequestMetric)} durante o processamento
 *   das requisições (muito rápido, não bloqueante).
 * - Periodicamente o método {@link #flush()} é executado (agendado) para persistir
 *   em lote as métricas acumuladas, delegando a escrita para o
 *   {@link RequestMetricRepository}.
 * <p>
 * A persistência é executada dentro de uma transação (@Transactional) no método
 * {@link #flush()} para garantir atomicidade do lote.
 */
@ApplicationScoped
public class MetricsService {

    // fila thread-safe com métricas a persistir
    private final Queue<RequestMetric> queue = new ConcurrentLinkedQueue<>();

    @Inject
    RequestMetricRepository requestMetricRepository;

    /**
     * Enfileira uma métrica para persistência posterior.
     * <p>
     * Este método deve ser chamado no contexto da requisição (por exemplo, em um filtro),
     * e deve executar rapidamente: a métrica é apenas adicionada a uma fila concorrente.
     *
     * @param metric métrica de requisição a ser registrada; se nula, a chamada é ignorada
     */
    public void enqueue(RequestMetric metric) {
        // operação rápida; não bloqueia o fluxo da requisição
        if (metric == null) return;
        queue.add(metric);
    }

    /**
     * Persiste em lote todas as métricas que estavam na fila no momento da chamada.
     * <p>
     * - Agendado para executar periodicamente (veja a anotação @Scheduled).
     * - Executado dentro de uma transação (@Transactional) para garantir consistência
     *   do lote.
     * - Coleta todas as métricas atualmente na fila e faz uma chamada única ao
     *   repositório para gravar o lote (eficiente para alto volume).
     */
    @Scheduled(every = "5s")
    @Transactional
    public void flush() {
        List<RequestMetric> batch = new ArrayList<>();
        RequestMetric m;
        while ((m = queue.poll()) != null) {
            batch.add(m);
        }

        if (!batch.isEmpty()) {
            requestMetricRepository.salvar(batch);
        }
    }
}
