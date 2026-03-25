package com.investimento.service.impl;

import com.investimento.api.dto.PerfilRiscoResponse;
import com.investimento.api.exception.ResourceNotFoundException;
import com.investimento.domain.entity.InvestimentoHistorico;
import com.investimento.domain.entity.Produto;
import com.investimento.repository.InvestimentoHistoricoRepository;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PerfilRiscoServiceImplTest {

    @Test
    void shouldThrowWhenCustomerHasNoHistory() throws Exception {
        InvestimentoHistoricoRepository repository = mock(InvestimentoHistoricoRepository.class);
        when(repository.findByClienteId(1L)).thenReturn(List.of());

        PerfilRiscoServiceImpl service = new PerfilRiscoServiceImpl();
        inject(service, "investimentoHistoricoRepository", repository);

        assertThrows(ResourceNotFoundException.class, () -> service.calcularPerfil(1L));
    }

    @Test
    void shouldCalculateAggressiveProfile() throws Exception {
        InvestimentoHistoricoRepository repository = mock(InvestimentoHistoricoRepository.class);
        when(repository.findByClienteId(999L)).thenReturn(List.of(
                historico(999L, "30000", 5, 8),
                historico(999L, "85000", 4, 9),
                historico(999L, "45000", 6, 8)
        ));

        PerfilRiscoServiceImpl service = new PerfilRiscoServiceImpl();
        inject(service, "investimentoHistoricoRepository", repository);

        PerfilRiscoResponse response = service.calcularPerfil(999L);

        assertEquals(999L, response.clienteId());
        assertEquals("AGRESSIVO", response.perfil());
        assertEquals(85, response.pontuacao());
    }

    private InvestimentoHistorico historico(Long clienteId, String valor, int liquidezScore, int rentabilidadeScore) {
        Produto produto = new Produto();
        produto.liquidezScore = liquidezScore;
        produto.rentabilidadeScore = rentabilidadeScore;

        InvestimentoHistorico historico = new InvestimentoHistorico();
        historico.clienteId = clienteId;
        historico.valor = new BigDecimal(valor);
        historico.data = LocalDate.now();
        historico.produto = produto;
        return historico;
    }

    private void inject(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
