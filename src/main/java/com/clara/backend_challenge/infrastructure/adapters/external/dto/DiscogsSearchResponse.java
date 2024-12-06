package com.clara.backend_challenge.infrastructure.adapters.external.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DiscogsSearchResponse {
    private Pagination pagination;
    private List<DiscogsArtist> results;

    @Getter
    @Setter
    public static class DiscogsArtist {
        private Long id;
        private String title;
        private String thumb;
        private String resource_url;
    }
}
