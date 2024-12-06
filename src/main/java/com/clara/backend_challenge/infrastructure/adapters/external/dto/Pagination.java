package com.clara.backend_challenge.infrastructure.adapters.external.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Pagination {
    private int page;
    private int pages;
    private int per_page;
    private int items;
}
