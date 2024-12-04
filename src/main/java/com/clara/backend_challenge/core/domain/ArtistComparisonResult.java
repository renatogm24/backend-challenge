package com.clara.backend_challenge.core.domain;

import java.util.List;
import java.util.Objects;

public class ArtistComparisonResult {
    private List<ArtistComparisonData> artists;

    public ArtistComparisonResult() {
    }

    public ArtistComparisonResult(List<ArtistComparisonData> artists) {
        this.artists = artists;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArtistComparisonResult that = (ArtistComparisonResult) o;
        return Objects.equals(artists, that.artists);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(artists);
    }

    @Override
    public String toString() {
        return "ArtistComparisonResult{" +
                "artists=" + artists +
                '}';
    }

    public List<ArtistComparisonData> getArtists() {
        return artists;
    }

    public void setArtists(List<ArtistComparisonData> artists) {
        this.artists = artists;
    }
}
