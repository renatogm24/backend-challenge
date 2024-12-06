package com.clara.backend_challenge.infrastructure.persistence.repository;

import com.clara.backend_challenge.infrastructure.persistence.entity.ReleaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface ReleaseRepositoryJpa extends JpaRepository<ReleaseEntity, Long> {

    @Query("SELECT r.id FROM ReleaseEntity r JOIN r.artists a WHERE a.id = :artistId")
    Set<Long> findAllIdsByArtistId(@Param("artistId") Long artistId);

}
