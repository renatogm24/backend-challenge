package com.clara.backend_challenge.application.service;

import com.clara.backend_challenge.core.domain.Artist;
import com.clara.backend_challenge.core.domain.Release;
import com.clara.backend_challenge.core.exceptions.ArtistNotFoundException;
import com.clara.backend_challenge.core.ports.output.ArtistRepository;
import com.clara.backend_challenge.core.ports.output.DiscogsApiClient;
import com.clara.backend_challenge.core.ports.output.ReleaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ArtistAsyncServiceTest {

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private ReleaseRepository releaseRepository;

    @Mock
    private DiscogsApiClient discogsApiClient;

    @InjectMocks
    private ArtistAsyncService artistAsyncService;

    private List<Release> testReleases;

    private Artist testArtist;

    @BeforeEach
    void setUp() {
        testReleases = List.of(
                new Release(1L, "Release 1", "Album", 2021, "thumbnail1", List.of("Genre1")),
                new Release(2L, "Release 2", "Album", 2022, "thumbnail2", List.of("Genre2"))
        );

        testArtist = new Artist(1L, "Test Artist", "Test Profile", "Test Image", List.of());
    }

    @Test
    void findArtistWithRetries_shouldReturnArtist_whenArtistExists() {
        when(artistRepository.findById(1L)).thenReturn(Optional.of(testArtist));

        Artist result = artistAsyncService.findArtistWithRetries(1L);

        assertThat(result).isEqualTo(testArtist);
        verify(artistRepository, times(1)).findById(1L);
    }

    @Test
    void findArtistWithRetries_shouldThrowException_whenArtistNotFound() {
        when(artistRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ArtistNotFoundException.class, () -> artistAsyncService.findArtistWithRetries(1L));
        verify(artistRepository, times(1)).findById(1L);
    }

    @Test
    void enrichReleases_shouldEnrichGenres_whenDetailsAreAvailable() {
        when(discogsApiClient.getReleaseDetails(anyLong()))
                .thenAnswer(invocation -> new Release((Long) invocation.getArgument(0), "", "", 2021, "", List.of("Genre1", "Genre2")));

        artistAsyncService.enrichReleases(testReleases);

        assertThat(testReleases.get(0).getGenres()).containsExactly("Genre1", "Genre2");
        assertThat(testReleases.get(1).getGenres()).containsExactly("Genre1", "Genre2");
        verify(discogsApiClient, times(2)).getReleaseDetails(anyLong());
    }

    @Test
    void enrichReleasesAsync_shouldUpdateReleasesAndArtist_whenGenresAreEnriched() {
        List<Long> releaseIds = testReleases.stream().map(Release::getId).toList();

        when(releaseRepository.findAllById(releaseIds)).thenReturn(testReleases);
        when(artistRepository.findById(1L)).thenReturn(Optional.of(testArtist));
        when(discogsApiClient.getReleaseDetails(anyLong()))
                .thenAnswer(invocation -> new Release((Long) invocation.getArgument(0), "", "", 2021, "", List.of("Genre1", "Genre2")));

        artistAsyncService.enrichReleasesAsync(testReleases, 1L);

        verify(releaseRepository, times(1)).saveAll(testReleases);
        verify(artistRepository, times(1)).save(testArtist);
    }

    @Test
    void enrichReleasesAsync_shouldNotUpdate_whenNoGenresEnriched() {
        testReleases.forEach(release -> release.setGenres(null));

        artistAsyncService.enrichReleasesAsync(testReleases, 1L);

        verify(releaseRepository, never()).saveAll(anyList());
        verify(artistRepository, never()).save(any(Artist.class));
    }

}