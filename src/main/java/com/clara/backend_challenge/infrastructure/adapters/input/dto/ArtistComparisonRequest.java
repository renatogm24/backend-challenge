package com.clara.backend_challenge.infrastructure.adapters.input.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ArtistComparisonRequest {
    private List<Long> artistIds;
}
