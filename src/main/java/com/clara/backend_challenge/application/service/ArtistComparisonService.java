package com.clara.backend_challenge.application.service;

import com.clara.backend_challenge.core.domain.ArtistComparisonResult;
import com.clara.backend_challenge.core.exceptions.InvalidArtistComparisonException;
import com.clara.backend_challenge.core.ports.input.ArtistComparisonUseCase;
import com.clara.backend_challenge.core.ports.output.ArtistRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ArtistComparisonService implements ArtistComparisonUseCase {

    private final ArtistRepository artistRepository;

    public ArtistComparisonService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @Override
    public ArtistComparisonResult compareArtists(List<Long> artistIds) {
        if (artistIds == null || artistIds.size() < 2) {
            log.warn("Invalid comparison request: Less than two artist IDs provided.");
            throw new InvalidArtistComparisonException("At least two artist IDs are required for comparison.");
        }

        log.info("Starting comparison for artist IDs: {}", artistIds);

        List<ArtistComparisonResult.ArtistData> comparisonData = artistIds.stream()
                .map(this::buildComparisonData)
                .toList();

        log.info("Comparison completed for artist IDs: {}", artistIds);
        return new ArtistComparisonResult(comparisonData);
    }

    private ArtistComparisonResult.ArtistData buildComparisonData(Long artistId) {
        log.debug("Building comparison data for artist ID: {}", artistId);

        String artistName = artistRepository.findNameById(artistId)
                .orElseThrow(() -> {
                    log.error("Artist with ID {} not found", artistId);
                    return new InvalidArtistComparisonException("Artist with ID " + artistId + " not found");
                });

        int numberOfReleases = artistRepository.countReleasesByArtistId(artistId);
        int activeYears = artistRepository.calculateActiveYearsByArtistId(artistId);
        List<String> commonGenres = artistRepository.findMostCommonGenresByArtistId(artistId);

        log.debug("Artist ID {} - Name: {}, Releases: {}, Active Years: {}, Common Genres: {}",
                artistId, artistName, numberOfReleases, activeYears, commonGenres);

        return new ArtistComparisonResult.ArtistData(
                artistId,
                artistName,
                numberOfReleases,
                activeYears,
                commonGenres
        );
    }
}
