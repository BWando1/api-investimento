package com.investimento.service;

import com.investimento.api.dto.PerfilRiscoResponse;

public interface PerfilRiscoService {

    PerfilRiscoResponse calcularPerfil(Long clienteId);
}
