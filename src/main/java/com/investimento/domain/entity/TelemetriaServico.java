package com.investimento.domain.entity;

import com.investimento.domain.converter.OffsetDateTimeStringConverter;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

@Entity
@Table(name = "telemetria_servico")
public class TelemetriaServico extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotBlank
    @Column(nullable = false, name = "nome_servico", unique = true)
    public String nomeServico;

    @NotNull
    @Min(0)
    @Column(nullable = false, name = "quantidade_chamadas")
    public Long quantidadeChamadas;

    @NotNull
    @Min(0)
    @Column(nullable = false, name = "tempo_total_resposta_ms")
    public Long tempoTotalRespostaMs;

    @NotNull
    @Convert(converter = OffsetDateTimeStringConverter.class)
    @Column(nullable = false, name = "ultima_atualizacao")
    public OffsetDateTime ultimaAtualizacao;
}
