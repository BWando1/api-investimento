package com.investimento.api.dto;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        int page,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {

    public static <T> PageResponse<T> of(List<T> content, int page, int pageSize, long totalElements) {
        int totalPages = pageSize > 0 ? (int) Math.ceil((double) totalElements / pageSize) : 0;
        return new PageResponse<>(
                content,
                page,
                pageSize,
                totalElements,
                totalPages,
                page == 0,
                page >= totalPages - 1
        );
    }
}
