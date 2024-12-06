package com.clara.backend_challenge.application.service;

import com.clara.backend_challenge.core.domain.ArtistComparisonResult;
import com.clara.backend_challenge.core.exceptions.InvalidArtistComparisonException;
import com.clara.backend_challenge.core.ports.output.ArtistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArtistComparisonServiceTest {

    @Mock
    private ArtistRepository artistRepository;

    @InjectMocks
    private ArtistComparisonService artistComparisonService;

    private static final Long ARTIST_ID_1 = 1L;
    private static final Long ARTIST_ID_2 = 2L;
    private static final List<Long> ARTIST_IDS = List.of(ARTIST_ID_1, ARTIST_ID_2);

    private ArtistComparisonResult.ArtistData artistData1;
    private ArtistComparisonResult.ArtistData artistData2;

    @BeforeEach
    void setUp() {
        artistData1 = new ArtistComparisonResult.ArtistData(ARTIST_ID_1, "Artist One", 10, 5, List.of("Rock", "Pop"));
        artistData2 = new ArtistComparisonResult.ArtistData(ARTIST_ID_2, "Artist Two", 8, 3, List.of("Jazz", "Blues"));
    }

    @Test
    void compareArtists_shouldReturnComparisonResult_whenValidArtistIdsProvided() {
        when(artistRepository.findNameById(ARTIST_ID_1)).thenReturn(Optional.of("Artist One"));
        when(artistRepository.findNameById(ARTIST_ID_2)).thenReturn(Optional.of("Artist Two"));
        when(artistRepository.countReleasesByArtistId(ARTIST_ID_1)).thenReturn(10);
        when(artistRepository.countReleasesByArtistId(ARTIST_ID_2)).thenReturn(8);
        when(artistRepository.calculateActiveYearsByArtistId(ARTIST_ID_1)).thenReturn(5);
        when(artistRepository.calculateActiveYearsByArtistId(ARTIST_ID_2)).thenReturn(3);
        when(artistRepository.findMostCommonGenresByArtistId(ARTIST_ID_1)).thenReturn(List.of("Rock", "Pop"));
        when(artistRepository.findMostCommonGenresByArtistId(ARTIST_ID_2)).thenReturn(List.of("Jazz", "Blues"));

        ArtistComparisonResult result = artistComparisonService.compareArtists(ARTIST_IDS);

        assertThat(result.getArtists()).hasSize(2);
        assertThat(result.getArtists().get(0)).isEqualTo(artistData1);
        assertThat(result.getArtists().get(1)).isEqualTo(artistData2);

        verify(artistRepository).findNameById(ARTIST_ID_1);
        verify(artistRepository).findNameById(ARTIST_ID_2);
    }

    @Test
    void compareArtists_shouldThrowException_whenLessThanTwoArtistIdsProvided() {
        List<Long> invalidArtistIds = List.of(ARTIST_ID_1);

        assertThatThrownBy(() -> artistComparisonService.compareArtists(invalidArtistIds))
                .isInstanceOf(InvalidArtistComparisonException.class)
                .hasMessage("At least two artist IDs are required for comparison.");

        verifyNoInteractions(artistRepository);
    }

    @Test
    void compareArtists_shouldThrowException_whenArtistIdNotFound() {
        when(artistRepository.findNameById(ARTIST_ID_1)).thenReturn(Optional.of("Artist One"));
        when(artistRepository.findNameById(ARTIST_ID_2)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> artistComparisonService.compareArtists(ARTIST_IDS))
                .isInstanceOf(InvalidArtistComparisonException.class)
                .hasMessage("Artist with ID 2 not found");

        verify(artistRepository).findNameById(ARTIST_ID_1);
        verify(artistRepository).findNameById(ARTIST_ID_2);
    }

    @Test
    void buildComparisonData_shouldReturnValidArtistData() {
        when(artistRepository.findNameById(ARTIST_ID_1)).thenReturn(Optional.of("Artist One"));
        when(artistRepository.countReleasesByArtistId(ARTIST_ID_1)).thenReturn(10);
        when(artistRepository.calculateActiveYearsByArtistId(ARTIST_ID_1)).thenReturn(5);
        when(artistRepository.findMostCommonGenresByArtistId(ARTIST_ID_1)).thenReturn(List.of("Rock", "Pop"));

        ArtistComparisonResult.ArtistData result = artistComparisonService.buildComparisonData(ARTIST_ID_1);

        assertThat(result).isEqualTo(artistData1);

        verify(artistRepository).findNameById(ARTIST_ID_1);
        verify(artistRepository).countReleasesByArtistId(ARTIST_ID_1);
        verify(artistRepository).calculateActiveYearsByArtistId(ARTIST_ID_1);
        verify(artistRepository).findMostCommonGenresByArtistId(ARTIST_ID_1);
    }
}