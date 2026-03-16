package com.investimento.api.exception;

import com.investimento.api.dto.ApiResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class BusinessExceptionMapper implements ExceptionMapper<BusinessException> {

    @Override
    public Response toResponse(BusinessException e) {
        return Response.status(e.getStatusCode())
                .type(MediaType.APPLICATION_JSON)
                .entity(ApiResponse.error(e.getMessage()))
                .build();
    }
}
