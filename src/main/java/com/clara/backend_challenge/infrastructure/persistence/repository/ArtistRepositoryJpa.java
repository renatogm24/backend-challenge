package com.clara.backend_challenge.infrastructure.persistence.repository;

import com.clara.backend_challenge.infrastructure.persistence.entity.ArtistEntity;
import com.clara.backend_challenge.infrastructure.persistence.entity.ReleaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArtistRepositoryJpa extends JpaRepository<ArtistEntity, Long> {

    @Query("SELECT COUNT(r) " +
            "FROM ReleaseEntity r " +
            "JOIN r.artists a " +
            "WHERE a.id = :artistId")
    int countReleasesByArtistId(@Param("artistId") Long artistId);

    @Query("SELECT a.name FROM ArtistEntity a WHERE a.id = :artistId")
    String findNameById(@Param("artistId") Long artistId);

    @Query("SELECT r FROM ReleaseEntity r " +
            "JOIN r.artists a " +
            "WHERE a.id = :artistId AND r.year IS NOT NULL " +
            "ORDER BY r.year DESC")
    Page<ReleaseEntity> findReleasesByArtistId(@Param("artistId") Long artistId, Pageable pageable);

    @Query("SELECT (MAX(r.year) - MIN(r.year) + 1) " +
            "FROM ReleaseEntity r " +
            "JOIN r.artists a " +
            "WHERE a.id = :artistId " +
            "AND r.year IS NOT NULL")
    int calculateActiveYearsByArtistId(@Param("artistId") Long artistId);

    @Query(value = """
        SELECT g.genre_name AS genre_name
        FROM artist_release ar
        JOIN release_genres g ON ar.release_id = g.release_id
        WHERE ar.artist_id = :artistId
        GROUP BY g.genre_name
        ORDER BY COUNT(g.genre_name) DESC
        LIMIT 5
    """, nativeQuery = true)
    List<String> findMostCommonGenresByArtistId(@Param("artistId") Long artistId);

}