package com.clara.backend_challenge.core.domain;

import java.util.List;
import java.util.Objects;

public class Release {
    private Long id;
    private String title;
    private String type;
    private Integer year;
    private String thumbnail;
    private List<Genre> genres;

    public Release() {
    }

    public Release(Long id, String title, String type, Integer year, String thumbnail, List<Genre> genres) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.year = year;
        this.thumbnail = thumbnail;
        this.genres = genres;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Release release = (Release) o;
        return Objects.equals(id, release.id) && Objects.equals(title, release.title) && Objects.equals(type, release.type) && Objects.equals(year, release.year) && Objects.equals(thumbnail, release.thumbnail) && Objects.equals(genres, release.genres);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, type, year, thumbnail, genres);
    }

    @Override
    public String toString() {
        return "Release{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", year=" + year +
                ", thumbnail='" + thumbnail + '\'' +
                ", genres=" + genres +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }
}
