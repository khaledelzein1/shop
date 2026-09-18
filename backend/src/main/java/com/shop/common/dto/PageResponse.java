package com.shop.common.dto;

import java.util.List;
import org.springframework.data.domain.Page;

/**
 * Enveloppe de pagination renvoyée par tous les endpoints de liste, plutôt
 * que d'exposer directement {@link Page} de Spring Data (détail d'implé
 * qu'on ne veut pas coupler au contrat API).
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last) {

    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast());
    }
}
