package com.investimento.domain.entity;

import com.investimento.domain.converter.LocalDateStringConverter;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "investimento_historico")
public class InvestimentoHistorico extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotNull
    @Column(nullable = false, name = "cliente_id")
    public Long clienteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id")
    public Produto produto;

    @NotBlank
    @Column(nullable = false)
    public String tipo;

    @NotNull
    @DecimalMin("0.01")
    @Column(nullable = false)
    public BigDecimal valor;

    @NotNull
    @DecimalMin("0.000001")
    @Column(nullable = false)
    public BigDecimal rentabilidade;

    @NotNull
    @Convert(converter = LocalDateStringConverter.class)
    @Column(nullable = false)
    public LocalDate data;
}
