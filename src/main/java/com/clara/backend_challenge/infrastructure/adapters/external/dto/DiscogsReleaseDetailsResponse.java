package com.clara.backend_challenge.infrastructure.adapters.external.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DiscogsReleaseDetailsResponse {

    private Long id;
    private String title;
    private Integer year;
    private List<String> genres;
}
