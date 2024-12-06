package com.clara.backend_challenge.application.service;

import com.clara.backend_challenge.core.domain.Artist;
import com.clara.backend_challenge.core.domain.Release;
import com.clara.backend_challenge.core.exceptions.ArtistNotFoundException;
import com.clara.backend_challenge.core.ports.input.ArtistSearchUseCase;
import com.clara.backend_challenge.core.ports.output.ArtistRepository;
import com.clara.backend_challenge.core.ports.output.DiscogsApiClient;
import com.clara.backend_challenge.core.ports.output.ReleaseRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Predicate;

@Slf4j
@Service
public class ArtistSearchService implements ArtistSearchUseCase {

    private final ArtistRepository artistRepository;
    private final ReleaseRepository releaseRepository;
    private final DiscogsApiClient discogsApiClient;
    private final ArtistAsyncService artistAsyncService;

    public ArtistSearchService(ArtistRepository artistRepository, ReleaseRepository releaseRepository, DiscogsApiClient discogsApiClient, ArtistAsyncService artistAsyncService) {
        this.artistRepository = artistRepository;
        this.releaseRepository = releaseRepository;
        this.discogsApiClient = discogsApiClient;
        this.artistAsyncService = artistAsyncService;
    }

    @Override
    public List<Artist> searchArtists(String name) {
        log.info("Searching for artists with name: {}", name);
        return discogsApiClient.searchArtists(name);
    }

    @Override
    @Transactional
    public Artist getArtistDetails(Long artistId, boolean fetchAll, boolean force, int limit, int page, int size) {
        log.info("Fetching artist details for ID: {}, fetchAll: {}, force: {}, limit: {}, page: {}, size: {}",
                artistId, fetchAll, force, limit, page, size);

        if (!force) {
            var optionalArtist = artistRepository.findById(artistId, PageRequest.of(page, size, Sort.by("year").descending()));
            if (optionalArtist.isPresent()) {
                log.info("Artist ID {} found in the database. Returning cached data.", artistId);
                return optionalArtist.get();
            }
        }

        Artist artist = fetchArtistFromApi(artistId);

        List<Release> releases = fetchAll
                ? fetchReleases(artistId, discogsApiClient.getTotalItemsReleaseByArtist(artistId))
                : fetchReleases(artistId, limit);

        Set<Long> existingReleaseIds = releaseRepository.findAllIdsByArtistId(artistId);

        List<Release> filteredReleases = releases.stream()
                .filter(distinctById(Release::getId))
                .filter(release -> !existingReleaseIds.contains(release.getId()))
                .toList();

        artist.setReleases(filteredReleases);

        artistRepository.save(artist);

        artistAsyncService.enrichReleasesAsync(filteredReleases, artistId);

        return artistRepository.findById(artistId, PageRequest.of(page, size, Sort.by("year").descending()))
                .orElse(artist);
    }

    private Artist fetchArtistFromApi(Long artistId) {
        log.info("Fetching artist details from API for ID: {}", artistId);
        return Optional.ofNullable(discogsApiClient.getArtistDetails(artistId))
                .orElseThrow(() -> {
                    log.error("Artist with ID {} not found in Discogs API.", artistId);
                    return new ArtistNotFoundException("Artist with ID " + artistId + " not found in Discogs API.");
                });
    }

    public List<Release> fetchReleases(Long artistId, int limit) {
        log.info("Fetching all releases for artist ID: {}", artistId);

        int pageSize = 50;
        int totalPages = (int) Math.ceil((double) limit / pageSize);
        List<Release> releases = Collections.synchronizedList(new ArrayList<>());

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int page = 0; page < totalPages; page++) {
                int currentPage = page;
                executor.submit(() -> {
                    List<Release> pageReleases = fetchPaginatedReleases(artistId, currentPage, pageSize);
                    releases.addAll(pageReleases);
                });
            }
        } catch (Exception e) {
            log.error("Error while fetching releases: {}", e.getMessage());
        }

        log.info("Fetched {} releases for artist ID: {}", releases.size(), artistId);
        return releases;
    }

    private List<Release> fetchPaginatedReleases(Long artistId, int page, int size) {
        log.info("Fetching paginated releases for artist ID: {}, page: {}, size: {}", artistId, page, size);
        return discogsApiClient.getReleasesByArtist(artistId, page, size);
    }

    private static <T> Predicate<T> distinctById(Function<? super T, ?> idExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(idExtractor.apply(t));
    }
}

