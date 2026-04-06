package com.investimento.repository;

import com.investimento.entity.RequestMetric;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class RequestMetricRepository implements PanacheRepository<RequestMetric> {
    
    public void salvar(List<RequestMetric> batch) {
        if (batch == null || batch.isEmpty()) {
            return;
        }
        for (RequestMetric metric : batch) {
            persist(metric);
        }
    }
}
