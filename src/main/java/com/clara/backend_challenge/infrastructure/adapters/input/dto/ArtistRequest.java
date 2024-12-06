package com.clara.backend_challenge.infrastructure.adapters.input.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArtistRequest {
    private boolean fetchAll = false;
    private boolean force = false;
    private int limit = 20;
    private int page = 0;
    private int size = 5;
}
