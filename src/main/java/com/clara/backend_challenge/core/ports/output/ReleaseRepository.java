package com.clara.backend_challenge.core.ports.output;

import com.clara.backend_challenge.core.domain.Release;

import java.util.List;
import java.util.Set;

public interface ReleaseRepository {
    Set<Long> findAllIdsByArtistId(Long artistId);
    List<Release> findAllById(List<Long> ids);
    List<Release> saveAll(List<Release> releases);
}
