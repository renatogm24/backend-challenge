package com.clara.backend_challenge.core.ports.output;

import com.clara.backend_challenge.core.domain.Artist;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ArtistRepository {
    Optional<Artist> findById(Long id);
    Optional<Artist> findById(Long id, Pageable pageable);
    Artist save(Artist artist);
    Optional<Artist> findByIdWithReleases(Long artistId);
    boolean existsById(Long id);
    int countReleasesByArtistId(Long artistId);
    int calculateActiveYearsByArtistId(Long artistId);
    List<String> findMostCommonGenresByArtistId(Long artistId);
    Optional<String> findNameById(Long artistId);
}
