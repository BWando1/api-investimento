package com.investimento.api.exception;

import com.investimento.api.dto.ApiResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionMapper.class);

    @Override
    public Response toResponse(Exception e) {
        // Let BusinessException be handled by its own mapper
        if (e instanceof BusinessException be) {
            return Response.status(be.getStatusCode())
                    .type(MediaType.APPLICATION_JSON)
                    .entity(ApiResponse.error(be.getMessage()))
                    .build();
        }

        LOG.error("Unexpected error", e);

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(ApiResponse.error("Erro interno do servidor. Tente novamente mais tarde."))
                .build();
    }
}
