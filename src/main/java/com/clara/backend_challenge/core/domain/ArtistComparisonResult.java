package com.clara.backend_challenge.core.domain;

import java.util.List;
import java.util.Objects;

public class ArtistComparisonResult {

    private List<ArtistData> artists;

    public ArtistComparisonResult() {
    }

    public ArtistComparisonResult(List<ArtistData> artists) {
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
        return Objects.hash(artists);
    }

    @Override
    public String toString() {
        return "ArtistComparisonResult{" +
                "artists=" + artists +
                '}';
    }

    public List<ArtistData> getArtists() {
        return artists;
    }

    public void setArtists(List<ArtistData> artists) {
        this.artists = artists;
    }

    public static class ArtistData {
        private Long id;
        private String name;
        private int numberOfReleases;
        private int activeYears;
        private List<String> commonGenres;

        public ArtistData() {
        }

        public ArtistData(Long id, String name, int numberOfReleases, int activeYears, List<String> commonGenres) {
            this.id = id;
            this.name = name;
            this.numberOfReleases = numberOfReleases;
            this.activeYears = activeYears;
            this.commonGenres = commonGenres;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ArtistData that = (ArtistData) o;
            return Objects.equals(getId(), that.getId());
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public String toString() {
            return "ArtistData{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", numberOfReleases=" + numberOfReleases +
                    ", activeYears=" + activeYears +
                    ", commonGenres=" + commonGenres +
                    '}';
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getNumberOfReleases() {
            return numberOfReleases;
        }

        public void setNumberOfReleases(int numberOfReleases) {
            this.numberOfReleases = numberOfReleases;
        }

        public int getActiveYears() {
            return activeYears;
        }

        public void setActiveYears(int activeYears) {
            this.activeYears = activeYears;
        }

        public List<String> getCommonGenres() {
            return commonGenres;
        }

        public void setCommonGenres(List<String> commonGenres) {
            this.commonGenres = commonGenres;
        }
    }
}

