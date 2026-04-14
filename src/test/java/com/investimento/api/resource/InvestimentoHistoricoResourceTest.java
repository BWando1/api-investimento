package com.investimento.api.resource;

import com.investimento.api.dto.ApiResponse;
import com.investimento.api.dto.InvestimentoHistoricoResponse;
import com.investimento.api.exception.ResourceNotFoundException;
import com.investimento.service.InvestimentoHistoricoService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InvestimentoHistoricoResourceTest {

    private InvestimentoHistoricoResource resource;
    private InvestimentoHistoricoService service;

    @BeforeEach
    void setUp() throws Exception {
        resource = new InvestimentoHistoricoResource();
        service = mock(InvestimentoHistoricoService.class);

        Field field = InvestimentoHistoricoResource.class.getDeclaredField("investimentoHistoricoService");
        field.setAccessible(true);
        field.set(resource, service);
    }

    @Test
    void shouldReturn200WithHistoricoList() {
        // Arrange
        Long clienteId = 999L;
        List<InvestimentoHistoricoResponse> historico = List.of(
                new InvestimentoHistoricoResponse(1L, "CDB", new BigDecimal("5000.00"), new BigDecimal("0.12"), LocalDate.of(2026, 1, 10)),
                new InvestimentoHistoricoResponse(2L, "Fundo Multimercado", new BigDecimal("85000.00"), new BigDecimal("0.17"), LocalDate.of(2026, 2, 20))
        );
        when(service.listarPorCliente(clienteId)).thenReturn(historico);

        // Act
        Response response = resource.listarPorCliente(clienteId);

        // Assert
        assertEquals(200, response.getStatus());

        @SuppressWarnings("unchecked")
        ApiResponse<List<InvestimentoHistoricoResponse>> body =
                (ApiResponse<List<InvestimentoHistoricoResponse>>) response.getEntity();

        assertTrue(body.success());
        assertNotNull(body.data());
        assertEquals(2, body.data().size());
        assertEquals("CDB", body.data().get(0).tipo());
        assertEquals(new BigDecimal("5000.00"), body.data().get(0).valor());
        assertEquals("Fundo Multimercado", body.data().get(1).tipo());
    }

    @Test
    void shouldReturn200WithEmptyList() {
        // Arrange
        Long clienteId = 1L;
        when(service.listarPorCliente(clienteId)).thenReturn(List.of());

        // Act
        Response response = resource.listarPorCliente(clienteId);

        // Assert
        assertEquals(200, response.getStatus());

        @SuppressWarnings("unchecked")
        ApiResponse<List<InvestimentoHistoricoResponse>> body =
                (ApiResponse<List<InvestimentoHistoricoResponse>>) response.getEntity();

        assertTrue(body.success());
        assertNotNull(body.data());
        assertTrue(body.data().isEmpty());
        verify(service).listarPorCliente(clienteId);
    }

    @Test
    void shouldPropagateResourceNotFoundException() {
        // Arrange
        Long clienteId = 404L;
        when(service.listarPorCliente(clienteId))
                .thenThrow(new ResourceNotFoundException("Nenhum histórico encontrado para o cliente " + clienteId));

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> resource.listarPorCliente(clienteId));
        verify(service).listarPorCliente(clienteId);
    }

    @Test
    void shouldDelegateToServiceWithCorrectClienteId() {
        // Arrange
        Long clienteId = 123L;
        when(service.listarPorCliente(clienteId)).thenReturn(List.of());

        // Act
        resource.listarPorCliente(clienteId);

        // Assert
        verify(service, times(1)).listarPorCliente(123L);
        verify(service, never()).listarPorCliente(argThat(id -> !id.equals(clienteId)));
    }
}
