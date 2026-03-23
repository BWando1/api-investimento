package com.investimento.service.impl;

import com.investimento.api.dto.TelemetriaPeriodoResponse;
import com.investimento.api.dto.TelemetriaResponse;
import com.investimento.api.dto.TelemetriaServicoResponse;
import com.investimento.domain.entity.TelemetriaServico;
import com.investimento.repository.TelemetriaServicoRepository;
import com.investimento.service.TelemetriaService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@ApplicationScoped
public class TelemetriaServiceImpl implements TelemetriaService {

    @Inject
    TelemetriaServicoRepository telemetriaServicoRepository;

    @Override
    public TelemetriaResponse obterTelemetria() {
        List<TelemetriaServicoResponse> servicos = telemetriaServicoRepository.listarOrdenadoPorNome().stream()
                .map(this::toResponse)
                .toList();

        OffsetDateTime inicio = telemetriaServicoRepository.menorAtualizacao();
        OffsetDateTime fim = telemetriaServicoRepository.maiorAtualizacao();

        LocalDate hoje = LocalDate.now();
        TelemetriaPeriodoResponse periodo = new TelemetriaPeriodoResponse(
                inicio != null ? inicio.toLocalDate() : hoje,
                fim != null ? fim.toLocalDate() : hoje
        );

        return new TelemetriaResponse(servicos, periodo);
    }

    @Override
    @Transactional
    public void registrarChamada(String nomeServico, long tempoRespostaMs) {
        TelemetriaServico telemetria = telemetriaServicoRepository.findByNomeServico(nomeServico)
                .orElseGet(() -> novaTelemetria(nomeServico));

        telemetria.quantidadeChamadas = telemetria.quantidadeChamadas + 1;
        telemetria.tempoTotalRespostaMs = telemetria.tempoTotalRespostaMs + Math.max(0L, tempoRespostaMs);
        telemetria.ultimaAtualizacao = OffsetDateTime.now();

        if (telemetria.id == null) {
            telemetriaServicoRepository.persist(telemetria);
        }
    }

    private TelemetriaServico novaTelemetria(String nomeServico) {
        TelemetriaServico telemetria = new TelemetriaServico();
        telemetria.nomeServico = nomeServico;
        telemetria.quantidadeChamadas = 0L;
        telemetria.tempoTotalRespostaMs = 0L;
        telemetria.ultimaAtualizacao = OffsetDateTime.now();
        return telemetria;
    }

    private TelemetriaServicoResponse toResponse(TelemetriaServico entity) {
        long media = entity.quantidadeChamadas > 0
                ? entity.tempoTotalRespostaMs / entity.quantidadeChamadas
                : 0L;

        return new TelemetriaServicoResponse(
                entity.nomeServico,
                entity.quantidadeChamadas,
                media
        );
    }
}
