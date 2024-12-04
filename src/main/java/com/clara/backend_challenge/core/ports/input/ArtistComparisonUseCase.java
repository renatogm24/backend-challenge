package com.clara.backend_challenge.core.ports.input;

import com.clara.backend_challenge.core.domain.ArtistComparisonResult;

import java.util.List;

public interface ArtistComparisonUseCase {
    ArtistComparisonResult compareArtists(List<Long> artistIds);
}
