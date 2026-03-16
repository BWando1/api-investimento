package com.investimento.domain.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Entity
@Table(name = "produto")
public class Produto extends PanacheEntityBase {

    @Id
    public Long id;

    @NotBlank
    @Column(nullable = false)
    public String nome;

    @NotBlank
    @Column(nullable = false)
    public String tipo;

    @NotNull
    @DecimalMin("0.000001")
    @Column(nullable = false)
    public BigDecimal rentabilidade;

    @NotBlank
    @Column(nullable = false)
    public String risco;

    @NotNull
    @Min(1)
    @Column(nullable = false, name = "prazo_min_meses")
    public Integer prazoMinMeses;

    @NotNull
    @Min(1)
    @Column(nullable = false, name = "prazo_max_meses")
    public Integer prazoMaxMeses;

    @NotNull
    @DecimalMin("0.01")
    @Column(nullable = false, name = "valor_minimo")
    public BigDecimal valorMinimo;

    @NotNull
    @Min(1)
    @Max(10)
    @Column(nullable = false, name = "liquidez_score")
    public Integer liquidezScore;

    @NotNull
    @Min(1)
    @Max(10)
    @Column(nullable = false, name = "rentabilidade_score")
    public Integer rentabilidadeScore;

    @NotNull
    @Column(nullable = false)
    public Boolean ativo;
}
