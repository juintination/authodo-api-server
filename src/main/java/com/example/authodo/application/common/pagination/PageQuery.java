package com.example.authodo.application.common.pagination;

public record PageQuery<TFilter>(
    int page,
    int size,
    TFilter filter
) {

}
