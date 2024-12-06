package com.clara.backend_challenge.infrastructure.adapters.external;

import com.clara.backend_challenge.core.domain.Artist;
import com.clara.backend_challenge.core.domain.Release;
import com.clara.backend_challenge.infrastructure.exceptions.DiscogsApiException;
import com.clara.backend_challenge.core.ports.output.DiscogsApiClient;
import com.clara.backend_challenge.infrastructure.adapters.external.dto.DiscogsArtistResponse;
import com.clara.backend_challenge.infrastructure.adapters.external.dto.DiscogsReleaseDetailsResponse;
import com.clara.backend_challenge.infrastructure.adapters.external.dto.DiscogsReleaseResponse;
import com.clara.backend_challenge.infrastructure.adapters.external.mappers.DiscogsArtistMapper;
import com.clara.backend_challenge.infrastructure.adapters.external.dto.DiscogsSearchResponse;
import com.clara.backend_challenge.infrastructure.adapters.external.mappers.DiscogsReleaseDetailsMapper;
import com.clara.backend_challenge.infrastructure.adapters.external.mappers.DiscogsReleaseMapper;
import com.clara.backend_challenge.infrastructure.config.DiscogsApiProperties;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Slf4j
@Component
public class DiscogsApiClientImpl implements DiscogsApiClient {

    private final WebClient webClient;
    private final String token;
    private final DiscogsArtistMapper artistMapper;
    private final DiscogsReleaseMapper releaseMapper;
    private final DiscogsReleaseDetailsMapper detailsMapper;

    public DiscogsApiClientImpl(WebClient.Builder webClientBuilder, DiscogsApiProperties properties,
                                DiscogsArtistMapper artistMapper, DiscogsReleaseMapper releaseMapper,
                                DiscogsReleaseDetailsMapper detailsMapper) {
        this.webClient = webClientBuilder.baseUrl(properties.getBaseUrl()).build();
        this.token = properties.getToken();
        this.artistMapper = artistMapper;
        this.releaseMapper = releaseMapper;
        this.detailsMapper = detailsMapper;
    }

    @Override
    @CircuitBreaker(name = "discogsApi", fallbackMethod = "fallbackSearchArtists")
    public List<Artist> searchArtists(String query) {
        log.info("Searching artists with query: {}", query);
        DiscogsSearchResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/database/search")
                        .queryParam("q", query)
                        .queryParam("type", "artist")
                        .queryParam("token", token)
                        .build())
                .retrieve()
                .bodyToMono(DiscogsSearchResponse.class)
                .block();

        if (response != null && response.getResults() != null) {
            log.info("Found {} artists for query: {}", response.getResults().size(), query);
            return response.getResults().stream()
                    .map(artistMapper::toDomain)
                    .toList();
        } else {
            log.warn("No artists found for query: {}", query);
            return List.of();
        }
    }

    @Override
    @CircuitBreaker(name = "discogsApi", fallbackMethod = "fallbackGetArtistDetails")
    public Artist getArtistDetails(Long artistId) {
        log.info("Fetching details for artist ID: {}", artistId);
        DiscogsArtistResponse artist = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/artists/{id}")
                        .queryParam("token", token)
                        .build(artistId))
                .retrieve()
                .bodyToMono(DiscogsArtistResponse.class)
                .block();

        if (artist == null) {
            log.error("Discogs API returned null for artist ID: {}", artistId);
            throw new DiscogsApiException("Discogs API returned null for artist ID: " + artistId, null);
        }

        log.info("Successfully fetched artist details for ID: {}", artistId);
        return artistMapper.toDomain(artist);
    }

    @Override
    @CircuitBreaker(name = "discogsApi", fallbackMethod = "fallbackGetReleasesByArtist")
    public List<Release> getReleasesByArtist(Long artistId, int page, int size) {
        log.info("Fetching releases for artist ID: {}, page: {}, size: {}", artistId, page, size);
        DiscogsReleaseResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/artists/{id}/releases")
                        .queryParam("sort", "year")
                        .queryParam("page", page + 1)
                        .queryParam("per_page", size)
                        .queryParam("token", token)
                        .build(artistId))
                .retrieve()
                .bodyToMono(DiscogsReleaseResponse.class)
                .block();

        if (response != null && response.getReleases() != null) {
            log.info("Found {} releases for artist ID: {}", response.getReleases().size(), artistId);
            return response.getReleases().stream()
                    .map(releaseMapper::toDomain)
                    .toList();
        } else {
            log.warn("No releases found for artist ID: {}", artistId);
            return List.of();
        }
    }

    @Override
    @CircuitBreaker(name = "discogsApi", fallbackMethod = "fallbackGetReleaseDetails")
    public Release getReleaseDetails(Long releaseId) {
        log.info("Fetching release details for release ID: {}", releaseId);
        DiscogsReleaseDetailsResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/releases/{id}")
                        .queryParam("token", token)
                        .build(releaseId))
                .retrieve()
                .bodyToMono(DiscogsReleaseDetailsResponse.class)
                .block();

        if (response != null) {
            log.info("Successfully fetched release details for ID: {}", releaseId);
            return detailsMapper.toDomain(response);
        } else {
            throw new EntityNotFoundException("No details found for release ID: " + releaseId);
        }
    }

    @Override
    @CircuitBreaker(name = "discogsApi", fallbackMethod = "fallbackGetTotalItemsRelease")
    public Integer getTotalItemsReleaseByArtist(Long artistId) {
        var releases = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/artists/{id}/releases")
                        .queryParam("sort", "year")
                        .queryParam("page", 0) // Discogs uses 1-based pagination
                        .queryParam("per_page", 5)
                        .queryParam("token", token)
                        .build(artistId))
                .retrieve()
                .bodyToMono(DiscogsReleaseResponse.class)
                .block();
        if (releases != null) {
            return releases.getPagination().getItems();
        } else {
            return 0;
        }
    }

    // Fallbacks
    private List<Artist> fallbackSearchArtists(String query, Throwable throwable) {
        log.warn("Fallback triggered for searchArtists with query: {}. Reason: {}", query, throwable.getMessage());
        return List.of();
    }

    private Artist fallbackGetArtistDetails(Long artistId, Throwable throwable) {
        log.warn("Fallback triggered for getArtistDetails with artist ID: {}. Reason: {}", artistId, throwable.getMessage());
        return null;
    }

    private List<Release> fallbackGetReleasesByArtist(Long artistId, int page, int size, Throwable throwable) {
        log.warn("Fallback triggered for getReleasesByArtist with artist ID: {}, page: {}, size: {}. Reason: {}", artistId, page, size, throwable.getMessage());
        return List.of();
    }

    private Release fallbackGetReleaseDetails(Long releaseId, Throwable throwable) {
        log.warn("Fallback triggered for getReleaseDetails with release ID: {}. Reason: {}", releaseId, throwable.getMessage());
        return null;
    }

    private Integer fallbackGetTotalItemsRelease(Long artistId, Throwable throwable) {
        log.warn("Fallback triggered for getTotalItemsReleaseByArtist with artist ID: {}. Reason: {}", artistId, throwable.getMessage());
        return 0;
    }
}
