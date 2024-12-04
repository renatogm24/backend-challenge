package com.clara.backend_challenge.core.ports.output;

import com.clara.backend_challenge.core.domain.Release;
import com.clara.backend_challenge.core.domain.Artist;

import java.util.List;
import java.util.Optional;

public interface ArtistRepository {
    Optional<Artist> findById(Long id);
    void save(Artist artist);
    boolean existsById(Long id);
    List<Release> findReleasesByArtistId(Long id);
}
