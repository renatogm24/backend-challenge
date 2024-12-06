package com.clara.backend_challenge.infrastructure.adapters.output;

import com.clara.backend_challenge.core.domain.Artist;
import com.clara.backend_challenge.core.ports.output.ArtistRepository;
import com.clara.backend_challenge.infrastructure.adapters.output.mappers.ArtistMapper;
import com.clara.backend_challenge.infrastructure.adapters.output.mappers.ReleaseMapper;
import com.clara.backend_challenge.infrastructure.persistence.entity.ReleaseEntity;
import com.clara.backend_challenge.infrastructure.persistence.repository.ArtistRepositoryJpa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ArtistRepositoryAdapter implements ArtistRepository {

    private final ArtistRepositoryJpa artistRepositoryJpa;
    private final ArtistMapper artistMapper;
    private final ReleaseMapper releaseMapper;

    public ArtistRepositoryAdapter(ArtistRepositoryJpa artistRepositoryJpa, ArtistMapper artistMapper, ReleaseMapper releaseMapper) {
        this.artistRepositoryJpa = artistRepositoryJpa;
        this.artistMapper = artistMapper;
        this.releaseMapper = releaseMapper;
    }

    @Override
    public Optional<Artist> findById(Long id) {
        return artistRepositoryJpa.findById(id)
                .map(artistMapper::toDomain);
    }

    @Override
    public Optional<Artist> findById(Long id, Pageable pageable) {
        return artistRepositoryJpa.findById(id).map(artistEntity -> {
            Page<ReleaseEntity> releasesPage = artistRepositoryJpa.findReleasesByArtistId(id, pageable);

            Artist artist = artistMapper.toDomain(artistEntity);
            artist.setReleases(releasesPage.getContent().stream()
                    .map(releaseMapper::toDomain)
                    .collect(Collectors.toList()));
            return artist;
        });
    }

    @Override
    public Artist save(Artist artist) {
        return artistMapper.toDomain(artistRepositoryJpa.save(artistMapper.toEntity(artist)));
    }

    @Override
    public Optional<Artist> findByIdWithReleases(Long artistId) {
        return artistRepositoryJpa.findById(artistId)
                .map(artistMapper::toDomain);
    }

    @Override
    public boolean existsById(Long id) {
        return artistRepositoryJpa.existsById(id);
    }

    @Override
    public int countReleasesByArtistId(Long artistId) {
        return artistRepositoryJpa.countReleasesByArtistId(artistId);
    }

    @Override
    public int calculateActiveYearsByArtistId(Long artistId) {
        return artistRepositoryJpa.calculateActiveYearsByArtistId(artistId);
    }

    @Override
    public List<String> findMostCommonGenresByArtistId(Long artistId) {
        return artistRepositoryJpa.findMostCommonGenresByArtistId(artistId);
    }

    @Override
    public Optional<String> findNameById(Long artistId) {
        return Optional.ofNullable(artistRepositoryJpa.findNameById(artistId));
    }
}
