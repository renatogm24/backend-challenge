package com.clara.backend_challenge.infrastructure.persistence.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@Entity
@Table(
        name = "releases",
        indexes = {
                @Index(name = "idx_release_year", columnList = "year")
        }
)
public class ReleaseEntity {

    @Id
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column
    private String type;

    @Column
    private Integer year;

    @Column
    private String thumbnail;

    @ManyToMany(mappedBy = "releases", fetch = FetchType.LAZY)
    private List<ArtistEntity> artists;

    @ElementCollection
    @CollectionTable(name = "release_genres", joinColumns = @JoinColumn(name = "release_id"))
    @Column(name = "genre_name")
    private List<String> genres;
}
