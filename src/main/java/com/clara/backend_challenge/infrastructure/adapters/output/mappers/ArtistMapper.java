package com.clara.backend_challenge.infrastructure.adapters.output.mappers;

import com.clara.backend_challenge.core.domain.Artist;

import com.clara.backend_challenge.infrastructure.persistence.entity.ArtistEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {ReleaseMapper.class})
public interface ArtistMapper {

    Artist toDomain(ArtistEntity entity);

    ArtistEntity toEntity(Artist domain);
}
