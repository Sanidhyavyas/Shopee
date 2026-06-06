package com.shopee.shopee_backend.dto;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class PagedResponseDto<T> {

    private List<T> data;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;

    public static <T> PagedResponseDto<T> of(Page<T> pageResult) {
        PagedResponseDto<T> response = new PagedResponseDto<>();
        response.data          = pageResult.getContent();
        response.page          = pageResult.getNumber();
        response.size          = pageResult.getSize();
        response.totalElements = pageResult.getTotalElements();
        response.totalPages    = pageResult.getTotalPages();
        response.last          = pageResult.isLast();
        return response;
    }
}
