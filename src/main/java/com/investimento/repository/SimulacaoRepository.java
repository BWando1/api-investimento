package com.investimento.repository;

import com.investimento.api.dto.SimulacaoPorProdutoDiaResponse;
import com.investimento.domain.entity.Simulacao;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class SimulacaoRepository implements PanacheRepository<Simulacao> {

    public List<SimulacaoPorProdutoDiaResponse> listarAgregadoPorProdutoDia() {
        return getEntityManager()
                .createQuery(
                        "SELECT new com.investimento.api.dto.SimulacaoPorProdutoDiaResponse(" +
                        "s.produtoNome, " +
                        "s.dataSimulacaoDate, " +
                        "COUNT(s.id), " +
                        "CAST(AVG(s.valorFinal) AS BigDecimal)) " +
                        "FROM Simulacao s " +
                        "GROUP BY s.produtoNome, s.dataSimulacaoDate " +
                        "ORDER BY s.dataSimulacaoDate DESC, s.produtoNome ASC",
                        SimulacaoPorProdutoDiaResponse.class
                )
                .getResultList();
    }
}
