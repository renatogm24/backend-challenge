package com.clara.backend_challenge.core.domain;

import java.util.List;
import java.util.Objects;

public class Release {
    private Long id;
    private String title;
    private String type;
    private Integer year;
    private String thumbnail;
    private List<String> genres;

    public Release() {
    }

    public Release(Long id, String title, String type, Integer year, String thumbnail, List<String> genres) {
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
        return Objects.equals(id, release.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
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

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }
}
