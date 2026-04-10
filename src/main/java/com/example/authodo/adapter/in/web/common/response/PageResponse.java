package com.example.authodo.adapter.in.web.common.response;

import java.util.List;
import lombok.Getter;

@Getter
public class PageResponse<T> {

    private final List<T> items;
    private final int page;
    private final int size;
    private final long totalCount;
    private final long totalPages;

    private PageResponse(
        List<T> items,
        int page,
        int size,
        long totalCount,
        long totalPages
    ) {
        this.items = items;
        this.page = page;
        this.size = size;
        this.totalCount = totalCount;
        this.totalPages = totalPages;
    }

    public static <T> PageResponse<T> of(
        List<T> items,
        int page,
        int size,
        long totalCount
    ) {
        long totalPages = (totalCount == 0) ? 0 : ((totalCount - 1) / size) + 1;

        return new PageResponse<>(
            items,
            page,
            size,
            totalCount,
            totalPages
        );
    }

}
