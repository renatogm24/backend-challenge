package com.clara.backend_challenge.infrastructure.adapters.output;

import com.clara.backend_challenge.core.domain.Release;
import com.clara.backend_challenge.core.ports.output.ReleaseRepository;
import com.clara.backend_challenge.infrastructure.adapters.output.mappers.ReleaseMapper;
import com.clara.backend_challenge.infrastructure.persistence.repository.ReleaseRepositoryJpa;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public class ReleaseRepositoryAdapter implements ReleaseRepository {

    private final ReleaseRepositoryJpa releaseRepository;
    private final ReleaseMapper releaseMapper;

    public ReleaseRepositoryAdapter(ReleaseRepositoryJpa releaseRepository, ReleaseMapper releaseMapper) {
        this.releaseRepository = releaseRepository;
        this.releaseMapper = releaseMapper;
    }

    @Override
    public Set<Long> findAllIdsByArtistId(Long artistId) {
        return releaseRepository.findAllIdsByArtistId(artistId);
    }

    @Override
    public List<Release> findAllById(List<Long> ids) {
        return releaseRepository.findAllById(ids)
                .stream().map(releaseMapper::toDomain).toList();
    }

    @Override
    public List<Release> saveAll(List<Release> releases) {
        var releasesEntities = releases.stream()
                .map(releaseMapper::toEntity).toList();
        return releaseRepository.saveAll(releasesEntities)
                .stream().map(releaseMapper::toDomain).toList();
    }
}
