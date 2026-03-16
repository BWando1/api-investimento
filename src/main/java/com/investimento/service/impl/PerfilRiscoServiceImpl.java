package com.investimento.service.impl;

import com.investimento.api.dto.PerfilRiscoResponse;
import com.investimento.api.dto.PerfilRiscoTipo;
import com.investimento.api.exception.ResourceNotFoundException;
import com.investimento.domain.entity.InvestimentoHistorico;
import com.investimento.repository.InvestimentoHistoricoRepository;
import com.investimento.service.PerfilRiscoService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class PerfilRiscoServiceImpl implements PerfilRiscoService {

    @Inject
    InvestimentoHistoricoRepository investimentoHistoricoRepository;

    @Override
    public PerfilRiscoResponse calcularPerfil(Long clienteId) {
        LocalDate dozesMesesAtras = LocalDate.now().minusMonths(12);
        List<InvestimentoHistorico> historicos =
                investimentoHistoricoRepository.findByClienteIdSince(clienteId, dozesMesesAtras);

        if (historicos.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Nenhum histórico de investimentos encontrado para o cliente " + clienteId + " nos últimos 12 meses.");
        }

        int volumeScore = calcularVolumeScore(historicos);
        int frequenciaScore = calcularFrequenciaScore(historicos);
        int preferenciaScore = calcularPreferenciaScore(historicos);

        int pontuacaoTotal = volumeScore + frequenciaScore + preferenciaScore;

        PerfilRiscoTipo perfil = classificarPerfil(pontuacaoTotal);
        String descricao = gerarDescricao(perfil, pontuacaoTotal, volumeScore, frequenciaScore, preferenciaScore);

        return new PerfilRiscoResponse(clienteId, perfil.name(), pontuacaoTotal, descricao);
    }

    // --- Score components ---

    /**
     * Volume score (0–40): based on total invested amount in the last 12 months.
     * < R$10k      → 10
     * R$10k–50k    → 20
     * R$50k–150k   → 30
     * > R$150k     → 40
     */
    private int calcularVolumeScore(List<InvestimentoHistorico> historicos) {
        BigDecimal totalVolume = historicos.stream()
                .map(h -> h.valor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalVolume.compareTo(new BigDecimal("10000")) < 0) return 10;
        if (totalVolume.compareTo(new BigDecimal("50000")) < 0) return 20;
        if (totalVolume.compareTo(new BigDecimal("150000")) < 0) return 30;
        return 40;
    }

    /**
     * Frequency score (0–30): based on number of investment records in the last 12 months.
     * ≤ 2          → 8
     * 3–6          → 15
     * 7–12         → 24
     * > 12         → 30
     */
    private int calcularFrequenciaScore(List<InvestimentoHistorico> historicos) {
        int count = historicos.size();
        if (count <= 2) return 8;
        if (count <= 6) return 15;
        if (count <= 12) return 24;
        return 30;
    }

    /**
     * Preference score (0–30): based on the client's product preference using
     * liquidezScore vs rentabilidadeScore from linked produtos.
     *
     * High rentabilidade preference (risk-tolerant) → high score.
     * High liquidez preference (conservative) → low score.
     *
     * Only considers records that have a linked produto.
     */
    private int calcularPreferenciaScore(List<InvestimentoHistorico> historicos) {
        List<InvestimentoHistorico> comProduto = historicos.stream()
                .filter(h -> h.produto != null)
                .toList();

        if (comProduto.isEmpty()) {
            // No product data — assign a neutral mid-score
            return 15;
        }

        double avgLiquidez = comProduto.stream()
                .mapToInt(h -> h.produto.liquidezScore)
                .average()
                .orElse(5.0);

        double avgRentabilidade = comProduto.stream()
                .mapToInt(h -> h.produto.rentabilidadeScore)
                .average()
                .orElse(5.0);

        double delta = avgRentabilidade - avgLiquidez;

        if (delta >= 2.0) return 30;   // Strong preference for high return = aggressive
        if (delta >= 0.5) return 24;   // Slightly prefers returns
        if (delta > -0.5) return 15;   // Balanced (within margin of ±0.5)
        if (delta > -2.0) return 8;    // Slightly prefers liquidity
        return 5;                       // Strong preference for liquidity = very conservative
    }

    // --- Classification ---

    private PerfilRiscoTipo classificarPerfil(int pontuacao) {
        if (pontuacao < 40) return PerfilRiscoTipo.CONSERVADOR;
        if (pontuacao < 70) return PerfilRiscoTipo.MODERADO;
        return PerfilRiscoTipo.AGRESSIVO;
    }

    private String gerarDescricao(PerfilRiscoTipo perfil, int total, int volume, int frequencia, int preferencia) {
        return switch (perfil) {
            case CONSERVADOR -> String.format(
                    "Perfil Conservador (score %d/100) — Prioriza segurança e liquidez. " +
                    "Volume: %d/40 | Frequência: %d/30 | Preferência de produto: %d/30.",
                    total, volume, frequencia, preferencia);
            case MODERADO -> String.format(
                    "Perfil Moderado (score %d/100) — Equilibra rentabilidade e segurança. " +
                    "Volume: %d/40 | Frequência: %d/30 | Preferência de produto: %d/30.",
                    total, volume, frequencia, preferencia);
            case AGRESSIVO -> String.format(
                    "Perfil Agressivo (score %d/100) — Busca máxima rentabilidade e aceita maior risco. " +
                    "Volume: %d/40 | Frequência: %d/30 | Preferência de produto: %d/30.",
                    total, volume, frequencia, preferencia);
        };
    }
}
