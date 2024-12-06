package com.clara.backend_challenge.application.service;

import com.clara.backend_challenge.core.domain.Artist;
import com.clara.backend_challenge.core.domain.Release;
import com.clara.backend_challenge.core.exceptions.ArtistNotFoundException;
import com.clara.backend_challenge.core.ports.output.ArtistRepository;
import com.clara.backend_challenge.core.ports.output.DiscogsApiClient;
import com.clara.backend_challenge.core.ports.output.ReleaseRepository;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ArtistAsyncService {

    private final ArtistRepository artistRepository;
    private final ReleaseRepository releaseRepository;
    private final DiscogsApiClient discogsApiClient;

    public ArtistAsyncService(ArtistRepository artistRepository, ReleaseRepository releaseRepository, DiscogsApiClient discogsApiClient) {
        this.artistRepository = artistRepository;
        this.releaseRepository = releaseRepository;
        this.discogsApiClient = discogsApiClient;
    }

    @Async
    @Transactional
    public void enrichReleasesAsync(List<Release> releases, Long artistId) {
        log.info("Starting asynchronous enrichment of genres for artist ID: {}", artistId);

        try {
            enrichReleases(releases);

            List<Release> filteredReleases = releases.stream()
                    .filter(release -> release.getGenres() != null && !release.getGenres().isEmpty())
                    .toList();

            if (filteredReleases.isEmpty()) {
                log.info("No releases with genres to process for artist ID: {}", artistId);
                return;
            }

            List<Long> releaseIds = filteredReleases.stream()
                    .map(Release::getId)
                    .toList();

            List<Release> existingReleases = releaseRepository.findAllById(releaseIds);
            Map<Long, Release> existingReleasesMap = existingReleases.stream()
                    .collect(Collectors.toMap(Release::getId, Function.identity()));

            filteredReleases.forEach(release -> {
                Release existingRelease = existingReleasesMap.get(release.getId());
                if (existingRelease != null) {
                    existingRelease.setGenres(release.getGenres());
                } else {
                    log.warn("Release ID {} not found. Skipping update.", release.getId());
                }
            });

            releaseRepository.saveAll(existingReleases);

            Artist artist = findArtistWithRetries(artistId);
            artist.setReleases(filteredReleases);
            artistRepository.save(artist);

            log.info("Completed asynchronous enrichment of genres for artist ID: {}", artistId);
        } catch (Exception e) {
            log.error("Error during asynchronous enrichment for artist ID: {}. Reason: {}", artistId, e.getMessage(), e);
        }
    }

    public void enrichReleases(List<Release> releases) {
        log.info("Enriching releases concurrently for {} releases.", releases.size());

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            releases.forEach(release -> executor.submit(() -> {
                try {
                    var details = discogsApiClient.getReleaseDetails(release.getId());
                    if (details != null) {
                        release.setGenres(details.getGenres());
                    }
                } catch (Exception e) {
                    log.warn("Failed to fetch or enrich release details for ID: {}. Reason: {}", release.getId(), e.getMessage());
                }
            }));
        } catch (Exception e) {
            log.error("Error during concurrent enrichment: {}", e.getMessage());
        }

        log.info("Completed enriching releases.");
    }

    @Retry(name = "artistRetry")
    public Artist findArtistWithRetries(Long artistId) {
        return artistRepository.findById(artistId)
                .orElseThrow(() -> {
                    log.error("Artist with ID {} not found during retries.", artistId);
                    return new ArtistNotFoundException("Artist with ID " + artistId + " not found");
                });
    }
}
