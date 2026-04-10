package com.example.authodo.application.common.pagination;

import java.util.List;

public record PageResult<T>(
    List<T> items,
    long totalCount
) {

}
