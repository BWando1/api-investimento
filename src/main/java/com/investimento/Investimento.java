package com.investimento;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "investimento")
public class Investimento extends PanacheEntity {

    @NotBlank
    @Column(nullable = false)
    public String nome;

    @NotBlank
    @Column(nullable = false)
    public String tipo;

    @NotNull
    @DecimalMin("0.01")
    @Column(nullable = false)
    public BigDecimal valor;

    @NotNull
    @Column(nullable = false, name = "data_aplicacao")
    public LocalDate dataAplicacao;
}
