package com.investimento.api.exception;

import com.investimento.api.dto.ApiResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionMapper.class);

    @Override
    public Response toResponse(Exception e) {
        // Let BusinessException be handled by its own mapper
        if (e instanceof BusinessException be) {
            return Response.status(be.getStatusCode())
                    .type(MediaType.APPLICATION_JSON)
                    .entity(ApiResponse.error(be.getMessage()))
                    .build();
        }

        LOG.error("event=unexpected_error exceptionType={} message={}", e.getClass().getSimpleName(), e.getMessage(), e);

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(ApiResponse.error("Erro interno do servidor. Tente novamente mais tarde."))
                .build();
    }
}
