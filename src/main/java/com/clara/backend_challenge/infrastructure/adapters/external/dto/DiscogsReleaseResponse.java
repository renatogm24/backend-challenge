package com.clara.backend_challenge.infrastructure.adapters.external.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DiscogsReleaseResponse {

    private Pagination pagination;
    private List<ReleaseDto> releases;

    @Getter
    @Setter
    public static class ReleaseDto {
        private Long id;
        private String title;
        private String type;
        private Integer year;
        private String thumb;
    }

}
