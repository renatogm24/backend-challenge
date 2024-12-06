package com.clara.backend_challenge.infrastructure.adapters.external.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DiscogsArtistResponse {

    private Long id;
    private String name;
    private String profile;
    private String imageUrl;
    private List<ImageDto> images;

    @Getter
    @Setter
    public static class ImageDto {
        private String type;
        private String uri;
    }

}
