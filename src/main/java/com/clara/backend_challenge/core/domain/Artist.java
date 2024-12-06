package com.clara.backend_challenge.core.domain;

import java.util.List;
import java.util.Objects;

public class Artist {
    private Long id;
    private String name;
    private String profile;
    private String primaryImage;
    private List<Release> releases;

    public Artist() {
    }

    public Artist(Long id, String name, String profile, String primaryImage, List<Release> releases) {
        this.id = id;
        this.name = name;
        this.profile = profile;
        this.primaryImage = primaryImage;
        this.releases = releases;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Artist artist = (Artist) o;
        return Objects.equals(id, artist.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Artist{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", profile='" + profile + '\'' +
                ", primaryImage='" + primaryImage + '\'' +
                ", albums=" + releases +
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

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getPrimaryImage() {
        return primaryImage;
    }

    public void setPrimaryImage(String primaryImage) {
        this.primaryImage = primaryImage;
    }

    public List<Release> getReleases() {
        return releases;
    }

    public void setReleases(List<Release> releases) {
        this.releases = releases;
    }
}
