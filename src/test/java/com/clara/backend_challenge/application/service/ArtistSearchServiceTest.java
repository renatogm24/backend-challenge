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
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArtistSearchServiceTest {

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private ReleaseRepository releaseRepository;

    @Mock
    private DiscogsApiClient discogsApiClient;

    @Mock
    private ArtistAsyncService artistAsyncService;

    @InjectMocks
    private ArtistSearchService artistSearchService;

    private static final Long ARTIST_ID = 1L;
    private static final String ARTIST_NAME = "Test Artist";
    private static final int PAGE = 0;
    private static final int SIZE = 10;
    private static final int LIMIT = 50;
    private Artist testArtist;
    private List<Release> testReleases;

    @BeforeEach
    void setUp() {
        testArtist = new Artist();
        testArtist.setId(ARTIST_ID);
        testArtist.setName(ARTIST_NAME);

        Release release1 = new Release(1L, "Release 1", "Album", 2021, "thumbnail1", null);
        Release release2 = new Release(2L, "Release 2", "Album", 2022, "thumbnail2", null);
        testReleases = List.of(release1, release2);
    }

    @Test
    void searchArtists_shouldReturnArtists_whenNameIsValid() {
        when(discogsApiClient.searchArtists(ARTIST_NAME)).thenReturn(List.of(testArtist));

        List<Artist> result = artistSearchService.searchArtists(ARTIST_NAME);

        assertThat(result).isNotEmpty().contains(testArtist);
        verify(discogsApiClient).searchArtists(ARTIST_NAME);
    }

    @Test
    void getArtistDetails_shouldReturnCachedArtist_whenForceIsFalseAndArtistExists() {
        when(artistRepository.findById(eq(ARTIST_ID), any(PageRequest.class))).thenReturn(Optional.of(testArtist));

        Artist result = artistSearchService.getArtistDetails(ARTIST_ID, false, false, LIMIT, PAGE, SIZE);

        assertThat(result).isEqualTo(testArtist);
        verify(artistRepository).findById(eq(ARTIST_ID), any(PageRequest.class));
        verifyNoInteractions(discogsApiClient, artistAsyncService);
    }

    @Test
    void getArtistDetails_shouldFetchFromApi_whenArtistDoesNotExistInDb() {
        when(artistRepository.findById(eq(ARTIST_ID), any(PageRequest.class))).thenReturn(Optional.empty());
        when(discogsApiClient.getArtistDetails(ARTIST_ID)).thenReturn(testArtist);
        when(discogsApiClient.getTotalItemsReleaseByArtist(ARTIST_ID)).thenReturn(LIMIT);
        when(releaseRepository.findAllIdsByArtistId(ARTIST_ID)).thenReturn(Set.of());
        when(discogsApiClient.getReleasesByArtist(eq(ARTIST_ID), anyInt(), anyInt())).thenReturn(testReleases);

        Artist result = artistSearchService.getArtistDetails(ARTIST_ID, true, false, LIMIT, PAGE, SIZE);

        assertThat(result.getReleases()).hasSize(2);
        verify(discogsApiClient).getArtistDetails(ARTIST_ID);
        verify(artistRepository).save(testArtist);
    }

    @Test
    void getArtistDetails_shouldThrowException_whenArtistNotFoundInApi() {
        when(discogsApiClient.getArtistDetails(ARTIST_ID)).thenReturn(null);

        assertThatThrownBy(() -> artistSearchService.getArtistDetails(ARTIST_ID, true, true, LIMIT, PAGE, SIZE))
                .isInstanceOf(ArtistNotFoundException.class)
                .hasMessage("Artist with ID 1 not found in Discogs API.");

        verify(discogsApiClient).getArtistDetails(ARTIST_ID);
    }

    @Test
    void fetchReleases_shouldFetchAllPages_whenFetchAllIsTrue() {
        when(discogsApiClient.getReleasesByArtist(eq(ARTIST_ID), anyInt(), eq(50)))
                .thenAnswer(invocation -> {
                    int page = invocation.getArgument(1);
                    int startId = page * 50 + 1;
                    return IntStream.range(startId, startId + 50)
                            .mapToObj(id -> new Release((long) id, "Release " + id, "Album", 2021, "thumbnail" + id, null))
                            .toList();
                });

        List<Release> result = artistSearchService.fetchReleases(ARTIST_ID, 100);

        assertThat(result).hasSize(100);
        verify(discogsApiClient, times(2)).getReleasesByArtist(eq(ARTIST_ID), anyInt(), eq(50));
    }
}