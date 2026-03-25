package com.investimento.domain.entity;

import com.investimento.domain.converter.OffsetDateTimeStringConverter;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "simulacao")
public class Simulacao extends PanacheEntity {

    @NotNull
    @Column(nullable = false, name = "cliente_id")
    public Long clienteId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    public Produto produto;

    @NotBlank
    @Column(nullable = false, name = "produto_nome")
    public String produtoNome;

    @NotBlank
    @Column(nullable = false, name = "tipo_produto")
    public String tipoProduto;

    @NotNull
    @DecimalMin("0.01")
    @Column(nullable = false, name = "valor_investido")
    public BigDecimal valorInvestido;

    @NotNull
    @DecimalMin("0.01")
    @Column(nullable = false, name = "valor_final")
    public BigDecimal valorFinal;

    @NotNull
    @DecimalMin("0.000001")
    @Column(nullable = false, name = "rentabilidade_efetiva")
    public BigDecimal rentabilidadeEfetiva;

    @NotNull
    @Min(1)
    @Column(nullable = false, name = "prazo_meses")
    public Integer prazoMeses;

    @NotNull
    @Convert(converter = OffsetDateTimeStringConverter.class)
    @Column(nullable = false, name = "data_simulacao")
    public OffsetDateTime dataSimulacao;
}
