package com.clara.backend_challenge.core.domain;

import java.util.List;
import java.util.Objects;

public class ArtistComparisonData {
    private Long id;
    private String name;
    private int numberOfReleases;
    private int activeYears;
    private List<String> commonGenres;

    public ArtistComparisonData() {
    }

    public ArtistComparisonData(Long id, String name, int numberOfReleases, int activeYears, List<String> commonGenres) {
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
        ArtistComparisonData that = (ArtistComparisonData) o;
        return numberOfReleases == that.numberOfReleases && activeYears == that.activeYears && Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(commonGenres, that.commonGenres);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, numberOfReleases, activeYears, commonGenres);
    }

    @Override
    public String toString() {
        return "ArtistComparisonData{" +
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
